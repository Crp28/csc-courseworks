package ca.utoronto.utm.mcs;
import dagger.Module;
import dagger.Provides;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

@Module
public class ServerModule {
    @Provides
    public HttpServer provideHttpServer(){
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
            return server;
        }
        catch(IOException e) {
            throw new RuntimeException("error creating server");
        }
        
    }
}
