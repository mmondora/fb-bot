package com.mondora;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;

@Controller
@RequestMapping("/")
public class HelloController {

    private static final String VALIDATION_TOKEN = "disse_la_vacca_al_mulo";
    private static final String PAGE_ACCESS_TOKEN = "EAALrRUp2rHEBAKkQQZBnfAUgpPSJtJJbcz2S1GOBq7UJq6zPidHcSuHEZAO5nDMwjTCxF28JlnAouKHJgyzcR28Tggz2OvQU6vfnHCMUbB0IuFXkxzPVpa2pXyaAg0wvaMkoYNO0AgRZCZA6PZBQlwGZCMTd1Qx7VVYy0DbvvKAwZDZD";

    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public HelloController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @GetMapping
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        }

        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());

        PagedList<Post> feed = facebook.feedOperations().getFeed();
        model.addAttribute("feed", feed);
        model.addAttribute("fbId", facebook.userOperations().getUserProfile().getId());
        model.addAttribute("fbName", facebook.userOperations().getUserProfile().getName());

        System.out.println("\n\nHey Hey Hey\n\n");
        System.out.println(facebook.userOperations().getIdsForBusiness().toString());
        System.out.println(facebook.userOperations().getUserProfile().getId());
        System.out.println(facebook.userOperations().getUserProfile().getName());
        sentInstantMessage(facebook.userOperations().getUserProfile().getId(),
                "Welcome from HelloController " + new Date()
        );
        return "hello";
    }

    @RequestMapping(path = "webhook", method = RequestMethod.GET)
    public ResponseEntity<String> webHook(WebRequest json) {
        System.out.println("Request " + json.toString());
        json.getParameterNames().forEachRemaining(o -> System.out.println(o + " " + json.getParameter(o)));

        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonFactory factory = mapper.getFactory();
//            JsonParser jp = factory.createParser(json);
            if (valid(json, "hub.mode", "subscribe") &&
                    valid(json, "hub.verify_token", VALIDATION_TOKEN)) {
                System.out.println("Validating webhook");
                return new ResponseEntity(json.getParameter("hub.challenge"), HttpStatus.OK);

            } else {
                System.out.println("Failed validation. Make sure the validation tokens match.");
                return new ResponseEntity("Failed validation. Make sure the validation tokens match.", HttpStatus.FORBIDDEN);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "webhook", method = RequestMethod.POST)
    public ResponseEntity<String> webHookPost(@RequestBody String json) {
        System.out.println("Request " + json);

        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        mapper.configure(JsonParser.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
//            String id = r.entries[0].messages[0].sender.id;
//            sentInstantMessage( id, " Ciao " + id );

            JsonNode node = mapper.readTree(json);
            String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
            String text = node.get("entry").get(0).get("messaging").get(0).get("message").get("text").asText();
            System.out.println( "Id "  + id + " text " + text );
            sentInstantMessage( id, " Hello " + text );
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean valid(WebRequest wr, String pname, String value) {
        return wr.getParameter(pname) != null && wr.getParameter(pname).equals(value);
    }

    public void sentInstantMessage(String id, String text) {
        try {                         //EAALrRUp2rHEBAH1ZAPIYfzzGHwRNDYLP0KICrOiKOCknfiMjQ2NmCgBb0Ud6QzKSRx2JUXi8YVvrj07ZBbxorZBkCbrQ6usvxMR2BHarZAGToYAPvea1bzHs6vKILL3YIkuNc8xgh23V7i07shswxRHgIPTUz5ftcgVXSY1ZA3gZDZD
            String url = "https://graph.facebook.com/v2.6/me/messages";
            URLConnection urlc = new URL(url).openConnection();
            ((HttpURLConnection) urlc).setRequestMethod("POST");
            urlc.setDoOutput(true);
            urlc.setRequestProperty("access_token", PAGE_ACCESS_TOKEN);

            String payload = "{" +
                    "recipient: { id: '" + id + "'}," +
                    "message: { text: '" + text + "'  }," +
                    "notification_type: 'REGULAR' " +
                    "}";
            try (OutputStream output = urlc.getOutputStream()) {
                output.write(payload.getBytes());
            }

            System.out.println("Fatto. Mandato. " + PAGE_ACCESS_TOKEN);
            System.out.println(payload);

            System.out.println("\nInput");
            System.out.println(convertStreamToString(urlc.getInputStream()));
            System.out.println("\nError");
            System.out.println(convertStreamToString(((HttpURLConnection) urlc).getErrorStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Verso la fine.");
    }

    //Request {"object":"page",
    // "entry":[
    // {"id":"292024547861549","time":1483453571715,"messaging":[
    //      {
    //      "sender":{"id":"1250148388409499"},
    //      "recipient":{"id":"292024547861549"},
    //      "timestamp":1483452914367,
    //      "message":{"mid":"mid.1483452914367:9d83738165","seq":16057,"text":"ppp"}}
    // ]}]
    @XmlRootElement
    class Request {
        String object;
        Entry[] entries;

        @Override
        public String toString() {
            return "Request{" +
                    "object='" + object + '\'' +
                    ", entries=" + Arrays.toString(entries) +
                    '}';
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public Entry[] getEntries() {
            return entries;
        }

        public void setEntries(Entry[] entries) {
            this.entries = entries;
        }
    }

    class Entry {
        String id;
        long time;
        Messaging[] messages;

        @Override
        public String toString() {
            return "Entry{" +
                    "id='" + id + '\'' +
                    ", time=" + time +
                    ", messages=" + Arrays.toString(messages) +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public Messaging[] getMessages() {
            return messages;
        }

        public void setMessages(Messaging[] messages) {
            this.messages = messages;
        }
    }

    class Messaging {
        Sender sender;
        Recipient recipient;
        Message message;

        @Override
        public String toString() {
            return "Messaging{" +
                    "sender=" + sender +
                    ", recipient=" + recipient +
                    ", message=" + message +
                    '}';
        }

        public Sender getSender() {
            return sender;
        }

        public void setSender(Sender sender) {
            this.sender = sender;
        }

        public Recipient getRecipient() {
            return recipient;
        }

        public void setRecipient(Recipient recipient) {
            this.recipient = recipient;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    class Message {
        String mid;
        String seq;
        String text;

        @Override
        public String toString() {
            return "Message{" +
                    "mid='" + mid + '\'' +
                    ", seq='" + seq + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    class Recipient {
        String id;

        @Override
        public String toString() {
            return "Recipient{" +
                    "id='" + id + '\'' +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    class Sender {
        String id;

        @Override
        public String toString() {
            return "Sender{" +
                    "id='" + id + '\'' +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}