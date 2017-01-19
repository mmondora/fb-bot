package com.mondora.facebook.sending.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mondora.Database;
import com.mondora.model.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by mmondora on 19/01/2017.
 */
public class PusherThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PusherThread.class);
    ObjectMapper mapper = new ObjectMapper();
    private boolean running = true;

    public static String creaJson(String id) {
        return "{\"object\":\"page\",\"entry\":[{\"messaging\":[{\"sender\":{\"id\":\"" + id + "\"}}]}]}";
//
//        String json = "{\n" +
//                "  \"object\" : \"page\",\n" +
//                "  \"entry\" : [ {\n" +
////                "    \"id\" : \"\",\n" +
////                "    \"time\" : \"\",\n" +
//                "    \"messaging\" : [ {\n" +
//                "      \"sender\" : {\n" +
//                "        \"id\" : \""+id+"\"\n" +
//                "      }\n" +
//                "    } ]\n" +
//                "  } ]\n" +
//                "}";
    }

    @Override
    public void run() {
        while (running)
            try {
                Thread.sleep(1000 * 60 * 5);
                Collection<FBUser> users = Database.listAllUsers();
                users.forEach(u -> {
                    try {
                        JsonNode node = creaIdMessengerDaB2b( u.b2b_id );
                        new FatturaNotification().run( node );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public void stop() {
        running = false;
    }

    public JsonNode creaIdMessengerDaB2b(String b2b_id) throws IOException {
        Optional<FBUser> id = Database.findUserByB2B( b2b_id );
        if( id.isPresent() )
            return mapper.readTree(creaJson(id.get().messenger_id));
        else
            LOG.error( "B2B user $1 is not present in messenger db" );
        return null;
    }

    public JsonNode creaJsonNode(String id) throws IOException {
        return mapper.readTree(creaJson(id));
    }
}
