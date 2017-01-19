package com.mondora;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mondora.facebook.Connector;
import com.mondora.facebook.PusherThread;
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

        String load = Connector.getId(node);
        assertEquals( id, load );
    }
}
