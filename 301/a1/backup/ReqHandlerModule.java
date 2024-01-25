package ca.utoronto.utm.mcs;
import org.neo4j.driver.*;
import dagger.Module;
import dagger.Provides;

@Module
public class ReqHandlerModule {
    @Provides
    public Driver provideDriver() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic( "neo4j", "123456" ));
        return driver;
    }

    @Provides
    public Session provideSession(Driver driver) {
        return driver.session();
    }
}
