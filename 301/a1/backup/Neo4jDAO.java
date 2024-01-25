package ca.utoronto.utm.mcs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONObject;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

// All your database transactions or queries should 
// go in this class
public class Neo4jDAO {
    private final Driver driver;
    private final Session session;
    // TODO Complete This Class

    @Inject
    public Neo4jDAO(Driver driver, Session session) {
        this.driver = driver;
        this.session = session;
        this.addConstraints();
    }

    public void addConstraints() {
        String c1, c2;
        c1 = "CREATE CONSTRAINT actor_unique IF NOT EXISTS FOR (a:Actor) REQUIRE a.actorId IS UNIQUE";
        c2 = "CREATE CONSTRAINT movie_unique IF NOT EXISTS FOR (m:Movie) REQUIRE m.movieId IS UNIQUE";
        this.session.run(c1);
        this.session.run(c2);
    }


    // the below methods throws Exception that are used by the server to send its status code:
    // 200 OK
    // 400 BAD REQUEST
    // 404 NOT FOUND
    // 500 INTERNAL SERVER ERROR

    public void addActor(String name, String actorId) throws Exception {
        String query;
        query = "CREATE (a:Actor {name: \"%s\", actorId: \"%s\"})";
        query = String.format(query, name, actorId);
        try {
            this.session.run(query);
        }
        catch (Neo4jException Neoe) {
            if (Neoe.code().equals("Neo.ClientError.Schema.ConstraintValidationFailed")){
                throw new CustomException("400", Neoe);
            }
            else {
                throw new CustomException("500", Neoe);
            }
        }
        catch (Exception e) {
            throw new CustomException("500", e);
        }
    }

    public void addMovie(String name, String movieId) throws Exception {
        String query;
        query = "CREATE (m:Movie {name: \"%s\", movieId: \"%s\"})";
        query = String.format(query, name, movieId);
        try {
            this.session.run(query);
            
        }
        catch (Neo4jException Neoe) {
            if (Neoe.code().equals("Neo.ClientError.Schema.ConstraintValidationFailed")){
                throw new CustomException("400", Neoe);
            }
            else {
                throw new CustomException("500", Neoe);
            }
        }
        catch (Exception e) {
            throw new CustomException("500", e);
        }
    }

    public void addRelationship(String actorId, String movieId) throws Exception {
        String query, checkActor, checkMovie;
        query = "MATCH (a1:Actor {actorId:\"%s\"}) MATCH (m1:Movie {movieId:\"%s\"}) WHERE not exists((a1)-[:ACTED_IN]->(m1)) CREATE (a1)-[r:ACTED_IN]->(m1) RETURN r";
        query = String.format(query, actorId, movieId);
        checkActor = "MATCH (a:Actor {actorId:\"%s\"}) RETURN a";
        checkActor = String.format(checkActor, actorId);
        checkMovie = "MATCH (m:Movie {movieId:\"%s\"}) RETURN m";
        checkMovie = String.format(checkMovie, movieId);
        try {
            Boolean actorbool = this.session.run(checkActor).hasNext();
            Boolean moviebool = this.session.run(checkMovie).hasNext();
            if (actorbool == false || moviebool == false) {
                throw new CustomException("404");
            }
            Boolean resultbool = this.session.run(query).hasNext();
            if (resultbool == false) {
                throw new CustomException("400");
            }
            
        }
        catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException("500", e);
        }
    }

    public JSONObject getActor(String actorId) throws Exception {
        JSONObject result = new JSONObject();
        String query1, query2;
        query1 = "MATCH (a:Actor) WHERE a.actorId = \"%s\" RETURN a.actorId as actorId, a.name as name";
        query1 = String.format(query1, actorId);
        query2 = "MATCH (a:Actor)-[:ACTED_IN]->(m:Movie) WHERE a.actorId = \"%s\" RETURN m.movieId as movieId";
        query2 = String.format(query2, actorId);

        try {
            List<Record> result1 = this.session.run(query1).list();
            if (result1.isEmpty()) {
                throw new CustomException("404");
            }
            for (Record r: result1) {
                result.put("actorId", r.asMap().get("actorId").toString());
                result.put("name", r.asMap().get("name").toString());
            }
            List<Record> result2 = this.session.run(query2).list();
            ArrayList<String> movies = new ArrayList<>();
            for (Record r: result2) {
                movies.add(r.asMap().get("movieId").toString());
            }
            result.put("movies", movies);
            return result;
        }
        catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException("500", e);
        }
    }

    public JSONObject getMovie(String movieId) throws Exception {
        JSONObject result = new JSONObject();
        String query1, query2;
        query1 = "MATCH (m:Movie) WHERE m.movieId = \"%s\" RETURN m.movieId as movieId, m.name as name";
        query1 = String.format(query1, movieId);
        query2 = "MATCH (a:Actor)-[:ACTED_IN]->(m:Movie) WHERE m.movieId = \"%s\" RETURN a.actorId as actorId";
        query2 = String.format(query2, movieId);

        try {
            List<Record> result1 = this.session.run(query1).list();
            if (result1.isEmpty()) {
                throw new CustomException("404");
            }
            for (Record r: result1) {
                result.put("movieId", r.asMap().get("movieId").toString());
                result.put("name", r.asMap().get("name").toString());
            }
            List<Record> result2 = this.session.run(query2).list();
            ArrayList<String> actors = new ArrayList<>();
            for (Record r: result2) {
                actors.add(r.asMap().get("actorId").toString());
            }
            result.put("actors", actors);
            return result;
        }
        catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException("500", e);
        }
    }

    public JSONObject hasRelationship(String actorId, String movieId) throws Exception {
        JSONObject result = new JSONObject();
        String query, checkActor, checkMovie;
        Boolean actorbool, moviebool, relationbool;
        query = "MATCH (a:Actor {actorId:\"%s\"})-[r:ACTED_IN]->(m:Movie {movieId:\"%s\"}) RETURN r";
        query = String.format(query, actorId, movieId);
        checkActor = "MATCH (a:Actor {actorId:\"%s\"}) RETURN a";
        checkActor = String.format(checkActor, actorId);
        checkMovie = "MATCH (m:Movie {movieId:\"%s\"}) RETURN m";
        checkMovie = String.format(checkMovie, movieId);
        try {
            actorbool = this.session.run(checkActor).hasNext();
            moviebool = this.session.run(checkMovie).hasNext();
            if (actorbool == false || moviebool == false) {
                throw new CustomException("404");
            }
            relationbool = this.session.run(query).hasNext();
            result.put("actorId", actorId);
            result.put("movieId", movieId);
            result.put("hasRelationship", relationbool);
            return result;
        }
        catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException("500", e);
        }
    }

    public Integer computeBaconNumber(String actorId) {
        String query, baconId;
        Integer baconnumber;
        baconId = "nm0000102";
        query = "MATCH p=shortestPath((a:Actor {actorId:\"%s\"})-[:ACTED_IN*]-(b:Actor {actorId:\"%s\"})) RETURN length(p)/2 as baconnumber";
        query = String.format(query, baconId, actorId);
        try {
            List<Record> result = this.session.run(query).list();
            if (result.isEmpty() || result.size() > 1) {
                throw new CustomException("404");
            }
            baconnumber = Integer.parseInt(result.get(0).asMap().get("baconnumber").toString());
            return baconnumber;
        }
        catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException("500", e);
        }
    }

    public JSONObject computeBaconPath(String actorId) throws Exception {
        String query, baconId;
        JSONObject json = new JSONObject();
        ArrayList<String> path = new ArrayList<>();
        baconId = "nm0000102";
        if (actorId.equals(baconId)) {
            path.add(baconId);
            json.put("baconPath", path);
            return json;
        }
        query = "MATCH path=shortestPath((a:Actor {actorId:\"%s\"})-[:ACTED_IN*]-(b:Actor {actorId:\"%s\"})) RETURN path";
        query = String.format(query, actorId, baconId);
        try {
            List<Record> result = this.session.run(query).list();
            if (result.isEmpty()) {
                throw new CustomException("404");
            }
            Path p = (Path) result.get(0).asMap().get("path");
            for (Node node:p.nodes()) {
                long id = node.id();
                String query_getinfo = "MATCH (n) WHERE ID(n)=%d RETURN n.actorId as actorId, n.movieId as movieId";
                query_getinfo = String.format(query_getinfo, id);
                List<Record> info = this.session.run(query_getinfo).list();
                for (Record r:info) {
                    Object actor = r.asMap().get("actorId");
                    Object movie = r.asMap().get("movieId");
                    if (actor != null) {
                        path.add(actor.toString());
                    }
                    else if (movie != null) {
                        path.add(movie.toString());
                    }
                }
            }
            json.put("baconPath", path);
            return json;
        }
        catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException("500", e);
        }

    }

    public void close() {
        this.driver.close();
    }
}

class CustomException extends RuntimeException {

    public CustomException(String errorMessage) {
        super(errorMessage);
    }
    public CustomException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}