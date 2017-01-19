package com.mondora;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mondora.facebook.sending.Sender;
import com.mondora.facebook.sending.model.PusherThread;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by mmondora on 19/01/2017.
 */
public class NodeTest {
    @Test
    public void testJsonCreate() throws IOException {
        String id = "Ammazzate";
        String json = PusherThread.creaJson( id );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        String load = Sender.getId( node );
        assertEquals( id, load );
    }
}
