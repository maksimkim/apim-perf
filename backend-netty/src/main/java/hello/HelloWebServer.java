package hello;

import java.io.File;
import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;

import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

public class HelloWebServer {

	static {
		ResourceLeakDetector.setLevel(Level.DISABLED);
	}

	private final int port;

	public HelloWebServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		// Configure the server.

		if (Epoll.isAvailable()) {
			doRun(new EpollEventLoopGroup(), EpollServerSocketChannel.class, IoMultiplexer.EPOLL);
		} else if (KQueue.isAvailable()) {
			doRun(new EpollEventLoopGroup(), KQueueServerSocketChannel.class, IoMultiplexer.KQUEUE);
		} else {
			doRun(new NioEventLoopGroup(), NioServerSocketChannel.class, IoMultiplexer.JDK);
		}
	}

	private void doRun(EventLoopGroup loopGroup, Class<? extends ServerChannel> serverChannelClass, IoMultiplexer multiplexer) throws InterruptedException, SSLException {
		try {
			InetSocketAddress inet = new InetSocketAddress(port);

			ServerBootstrap b = new ServerBootstrap();

			if (multiplexer == IoMultiplexer.EPOLL) {
				b.option(EpollChannelOption.SO_REUSEPORT, true);
			}
            
            File certificate = new File("server.pem");
            File privateKey = new File("server.key");
            SslContext sslContext = SslContextBuilder.forServer(certificate, privateKey).sslProvider(SslProvider.OPENSSL).build();
                        
			b.option(ChannelOption.SO_BACKLOG, 8192);
			b.option(ChannelOption.SO_REUSEADDR, true);
			b.group(loopGroup).channel(serverChannelClass).childHandler(new HelloServerInitializer(loopGroup.next(), sslContext));
			b.childOption(ChannelOption.SO_REUSEADDR, true);

			Channel ch = b.bind(inet).sync().channel();

			System.out.printf("Netty backend started. Listening on: %s%n", inet.toString());

			ch.closeFuture().sync();
		} finally {
			loopGroup.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 443;
		}
		new HelloWebServer(port).run();
	}
}
