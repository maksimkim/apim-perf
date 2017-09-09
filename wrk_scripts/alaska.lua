local f=io.open("/tmp/alaska_req_2kb.json","r")
local content = f:read("*all")
f:close()
    
wrk.method = "POST"
wrk.body   = content
wrk.headers["Content-Type"] = "application/json"
wrk.headers["Connection"] = "close"

function delay()
   return math.random(10, 700)
end