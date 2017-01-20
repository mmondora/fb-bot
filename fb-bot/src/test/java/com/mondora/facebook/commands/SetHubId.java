package com.mondora.facebook.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mondora.facebook.commands.b2b.SettingHubID;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by mmondora on 20/01/2017.
 */
public class SetHubId {

    @Test
    public void testSetHubId() throws IOException {

        String id = "c85724a8-0eb3-4692-a52b-53375e5da2e3";
        String secret = "0abb44c3-3b8c-4018-b6d4-de70e1d157d3";

        String msg = "{\n" +
                "  \"object\" : \"page\",\n" +
                "  \"entry\" : [ {\n" +
                "    \"id\" : \"387095934984311\",\n" +
                "    \"time\" : 1484867220699,\n" +
                "    \"messaging\" : [ {\n" +
                "      \"sender\" : {\n" +
                "        \"id\" : \"1253251894751382\"\n" +
                "      },\n" +
                "      \"recipient\" : {\n" +
                "        \"id\" : \"387095934984311\"\n" +
                "      },\n" +
                "      \"timestamp\" : 1484867220502,\n" +
                "      \"message\" : {\n" +
                "        \"mid\" : \"mid.1484867220502:a7e2bd1e90\",\n" +
                "        \"seq\" : 18109,\n" +
                "        \"text\" : \"sethubid "+id+" "+secret+"\"\n" +
                "      }\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}}";


        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(msg);

        SettingHubID set = new SettingHubID();
        String[] out = set.split( "sethubid c85724a8-0eb3-4692-a52b-53375e5da2e3 0abb44c3-3b8c-4018-b6d4-de70e1d157d3" );

        assertEquals( "sethubid", out[0]);
        assertEquals( id, out[1]);
        assertEquals( secret, out[2]);

    }


}
