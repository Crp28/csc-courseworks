package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {
    private Neo4jDAO neo;
    private ArrayList<String> commands;
    @Inject
    public ReqHandler() {
        ReqHandlerComponent component = DaggerReqHandlerComponent.create();
        this.neo = component.getNeo4jDAO();
        this.commands = new ArrayList<>();
        this.commands.add("addActor");
        this.commands.add("addMovie");
        this.commands.add("addRelationship");
        this.commands.add("getActor");
        this.commands.add("getMovie");
        this.commands.add("hasRelationship");
        this.commands.add("computeBaconNumber");
        this.commands.add("computeBaconPath");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException, CustomException {
        String[] uri = exchange.getRequestURI().toString().split("/");
        if (uri[1].equals("api") && uri[2].equals("v1") && this.commands.contains(uri[3]) && uri.length == 4) {
            if (uri[3].equals("addActor")) {
                // check request body
                
            }
        }
        else {
            String response = "Wrong URI";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
            
        
        
        
        
        // try {
            //     if (r.getRequestMethod().equals("PUT")) {
            //         this.handlePut(r);
            //     }
            //     if (r.getRequestMethod().equals("GET")) {
            //         this.handleGet(r);
            //     }
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }
        }

    }