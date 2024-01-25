package ca.utoronto.utm.mcs;

import dagger.Component;

import javax.inject.Singleton;

import com.sun.net.httpserver.HttpServer;

@Singleton
// TODO Uncomment The Line Below When You Have Implemented ServerModule 
@Component(modules = ServerModule.class)
public interface ServerComponent {

	public Server buildServer();
	HttpServer getHttpServer();
}
