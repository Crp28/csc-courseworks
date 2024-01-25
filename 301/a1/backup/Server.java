package ca.utoronto.utm.mcs;

import javax.inject.Inject;

import com.sun.net.httpserver.HttpServer;

import dagger.Provides;

public class Server {
    private final HttpServer server;
    private final ReqHandler handler;
    @Inject
    public Server() {
        ServerComponent scomponent = DaggerServerComponent.create();
        ReqHandlerComponent rcomponent = DaggerReqHandlerComponent.create();
        HttpServer server = scomponent.getHttpServer();
        ReqHandler handler = rcomponent.buildHandler();
        this.server = server;
        this.handler = handler;
    }

    public ReqHandler getHandler() {
        return this.handler;
    }
    
    public HttpServer getHttpServer() {
        return this.server;
    }
    
}
