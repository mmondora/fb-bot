package com.mondora;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Utils {

    public static String convertStreamToString(java.io.InputStream is) {
        if (is != null) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } else return "";
    }

    public static boolean valid(WebRequest wr, String pname, String value) {
        return wr.getParameter(pname) != null && wr.getParameter(pname).equals(value);
    }

    public static boolean valid(JsonNode wr, String pname, String value) {
        return wr != null && wr.get(pname) != null && wr.get(pname).textValue().equals(value);
    }

    public static boolean valid(JsonNode wr, String pname) {
        return wr != null && wr.get(pname) != null;
    }

    public static String toJson( Object o ) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
