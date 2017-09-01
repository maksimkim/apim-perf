package hello;
package io.netty.handler.ssl;

import java.util.concurrent.ScheduledExecutorService;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {

	private ScheduledExecutorService service;
    private SslContext sslCtx;

	public HelloServerInitializer(ScheduledExecutorService service, SslContext sslCtx) {
		this.service = service;
        this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
        p.addLast(this.sslCtx.newHandler(p.alloc()));
		p.addLast("encoder", new HttpResponseEncoder());
		p.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));
		p.addLast("handler", new HelloServerHandler(service));
	}
}
