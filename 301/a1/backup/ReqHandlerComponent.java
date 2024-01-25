package ca.utoronto.utm.mcs;

import dagger.Component;
import javax.inject.Singleton;

import org.neo4j.driver.*;

@Singleton
// TODO Uncomment The Line Below When You Have Implemented ReqHandlerModule 


@Component(modules = ReqHandlerModule.class)
public interface ReqHandlerComponent {

    public ReqHandler buildHandler();
    
    public Driver getDriver();
    public Session getSession();

    public Neo4jDAO getNeo4jDAO();
}
