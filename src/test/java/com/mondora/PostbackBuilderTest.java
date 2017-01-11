package com.mondora;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by mmondora on 11/01/2017.
 */
public class PostbackBuilderTest {

    @Test
    public void testBuilder() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        HelloController.TwoChoicePostback o = new HelloController.TwoChoicePostback();
        o.recipient.id = "2";
        o.message.attachment.payload
                .addElement( "rift", "Next-generation virtual reality", "https://www.oculus.com/en-us/rift/", "http://messengerdemo.parseapp.com/img/rift.png");
        o.message.attachment.payload.last().addWebURLButton("Open Web URL", "https://www.oculus.com/en-us/rift/");
        o.message.attachment.payload.last().addPostbackButton("Call Postback", "Payload for first bubble");

        String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        System.out.println(out);

        String text = new String(
                Files.readAllBytes( Paths.get(this.getClass().getResource("/twochoice.json").toURI() )));

//        String compareTo = "{\n" +
//                "  \"recipient\" : {\n" +
//                "    \"id\" : \"2\"\n" +
//                "  },\n" +
//                "  \"message\" : {\n" +
//                "    \"attachment\" : {\n" +
//                "      \"type\" : \"template\",\n" +
//                "      \"payload\" : {\n" +
//                "        \"template_type\" : \"generic\",\n" +
//                "        \"elements\" : [ {\n" +
//                "          \"title\" : \"rift\",\n" +
//                "          \"subtitle\" : \"Next-generation virtual reality\",\n" +
//                "          \"item_url\" : \"https://www.oculus.com/en-us/rift/\",\n" +
//                "          \"image_url\" : \"http://messengerdemo.parseapp.com/img/rift.png\",\n" +
//                "          \"buttons\" : [ {\n" +
//                "            \"type\" : \"web_url\",\n" +
//                "            \"url\" : \"https://www.oculus.com/en-us/rift/\",\n" +
//                "            \"title\" : \"Open Web URL\"\n" +
//                "          }, {\n" +
//                "            \"type\" : \"postback\",\n" +
//                "            \"title\" : \"Call Postback\",\n" +
//                "            \"payload\" : \"Payload for first bubble\"\n" +
//                "          } ]\n" +
//                "        } ]\n" +
//                "      }\n" +
//                "    }\n" +
//                "  }\n" +
//                "}";

//        out = out.replaceAll("\t", "    ");
        assertEquals( text, out );
    }
}
