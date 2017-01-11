package com.mondora;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Controller
@RequestMapping("/")
public class HelloController {

    private static final String VALIDATION_TOKEN = "disse_la_vacca_al_mulo";
    private static final String PAGE_ACCESS_TOKEN = "EAAKUm3TRZCxMBAJAlUbfIfh4EXJI9QCgTtIe1PIbGI3dcZBknORacCQEZBT6xD8mUlor23JgRtZABpMVaqndM4D09KUtmFX2MkvxyG3JAY4BC9opasgYcIp6tYMRFx6jTtx7HxozCYAJORg34qacDVfpZBis9fSFcTJQpSstkfgZDZD";
    private static final String FACEBOOK_API_VERSION = "v2.6";
    private static final Properties postbacks = new Properties();
//    private static Map<String, User> map = new HashMap<String, User>() {
//        @Override
//        public User put(String key, User value) {
//            System.out.println("Adding " + value);
//            return super.put(key, value);
//        }
//    };
    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public HelloController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }

    //https://www.facebook.com/profile.php?id=1361337251&fref=ts&ref=br_tf&dpr=2&ajaxpipe=1&ajaxpipe_token=AXhv8U0Mv1JxQvcq&no_script_path=1&quickling[version]=2762676%3B0%3B&__user=779953134&__a=1&__dyn=5V5yAW8-aFoFxp2u6aOGeFxqdhEK5EKiWFaay8VFLFwxBxC9V8CdwIhE98nwgUaqwHzQ4UJi28rxuF8WUPBKuEjKexKcxaFQ3uaVVojxCVFEKLGqu545Kuifz8gAUlwkEG9J7By8K48hxGbwYxyr_xLgkBx-F8oiV8FoKEWdxyayoPDGVt4gjG498lBU&__af=i0&__req=jsonp_2&__be=-1&__pc=PHASED%3ADEFAULT&__rev=2762676&__adt=2

    static String convertStreamToString(java.io.InputStream is) {
        if (is != null) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } else return "";
    }

    @GetMapping
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        }

//        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());

        String [] fields = { "id", "email",  "first_name", "last_name" };
        User userProfile = facebook.fetchObject("me", User.class, fields);

        PagedList<Post> feed = facebook.feedOperations().getFeed();
        model.addAttribute("feed", feed);
        model.addAttribute("fbId", userProfile.getId() );
        model.addAttribute("fbName", userProfile.getFirstName() + " " + userProfile.getLastName() );

        System.out.println("Facebook login");
//        System.out.println("\t"+facebook.userOperations().getUserProfile().getName());
//        System.out.println("\tgetIdsForBusiness " + facebook.userOperations().getIdsForBusiness().toString());
//        System.out.println("\tgetUserProfile " + facebook.userOperations().getUserProfile().getId());
//        System.out.println("Pages");
//        facebook.pageOperations().getAccounts().forEach(o -> System.out.println("\tPage " + o.getName() ));
//        sendTextMessage(facebook.userOperations().getUserProfile().getId(),
//                "Welcome from HelloController " + new Date()
//        );

//        System.out.println("\n\nAmici\n\n");
//        facebook.friendOperations().getFriends().forEach(
//                o -> System.out.println("getFriends " + o.getName() + "" + o.getId() + " " + o.getExtraData())
//        );

        //addToMap( facebook.userOperations().getUserProfile() );
        return "hello";
    }

//    @RequestMapping(path = "map", method = RequestMethod.GET)
//    public ResponseEntity<String> map() {
//        StringBuffer out = new StringBuffer();
//        map.forEach((k, o) -> out.append(o.toString()));
//        return new ResponseEntity<String>(out.toString(), HttpStatus.OK);
//    }

    @RequestMapping(path = "deauth", method = RequestMethod.GET)
    public ResponseEntity<String> deAuth(WebRequest json) {
        System.out.println("Request " + json.toString());
        return new ResponseEntity<String>(HttpStatus.OK);
    }

//    private void addToMap(org.springframework.social.facebook.api.User userProfile) {
//        System.out.println("Cover " + userProfile.getCover());
//        String id = userProfile.getId();
//        User u = new User();
//        u.profile_pic = userProfile.getCover().getSource();
//        u.last_name = userProfile.getLastName();
//        u.first_name = userProfile.getFirstName();
//        u.gender = userProfile.getGender();
//        u.locale = userProfile.getLocale().toString();
//        u.id = id;
//        map.put(id, u);
//    }

    @RequestMapping(path = "webhook", method = RequestMethod.GET)
    public ResponseEntity<String> webHook(WebRequest json) {
        System.out.println("GET /webhook");
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
        System.out.println("POST /webhook");
        System.out.println("Request " + json);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(json);
            if (valid(node, "object", "page")) {
//                if( valid(node, "postback") ) {
                    Strategy s = builder(node);
                    System.out.println( "Builder " + s.getClass() );
                    s.run(node);
//                }
//                System.out.println("Id " + id + " text " + text);
            } else {
                System.out.println("Ignored " + json);
            }
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Strategy builder(JsonNode node) {
        try {
            JsonNode n = node.get("entry").get(0).get("messaging").get(0).get("optin");
            if (n != null) return new Optin();

            n = node.get("entry").get(0).get("messaging").get(0).get("postback");
            if (n != null) return new PostBack();

            n = node.get("entry").get(0).get("messaging").get(0).get("read");
            if (n != null) return new Read();

            return new Message();
        } finally {

        }
    }

    private boolean valid(WebRequest wr, String pname, String value) {
        return wr.getParameter(pname) != null && wr.getParameter(pname).equals(value);
    }

    private boolean valid(JsonNode wr, String pname, String value) {
        return wr != null && wr.get(pname) != null && wr.get(pname).textValue().equals(value);
    }

    private boolean valid(JsonNode wr, String pname ) {
        return wr != null && wr.get(pname) != null;
    }

    @RequestMapping(path = "send", method = RequestMethod.POST)
    public ResponseEntity<String> send(@RequestParam String id, @RequestParam String text) {
        sendTextMessage(id, text);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public void sendTextMessage(String id, String text) {
        String payload = "{" +
                "\"recipient\": { \"id\": \"" + id + "\"}," +
                "\"message\": { \"text\": \"" + text + "\"  }" +
                "}";

        sendGenericMessage(payload);
    }


    public void sendStructuredMessage(String id, String text) {
        String uuid = UUID.randomUUID().toString();
        TwoChoicePostback o = new TwoChoicePostback();
        o.recipient.id = id;
        PBPayload payload = o.message.attachment.payload;
        payload.addElement("Agyo, nuovo documento", text, "https://app.agyo.io/console/index.html" );
        payload.last().addPostbackButton("Rifiuta", "Rifiuta@" + uuid );
        payload.last().addPostbackButton("Accetta", "Accetta@" + uuid );

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            String json =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            sendGenericMessage(json);
            postbacks.put( uuid, text );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    };

    public void sendGenericMessage(String payload) {
        new Thread(() -> {
            String id = Thread.currentThread().getName()+"/"+Thread.currentThread().getId();
            System.out.println(id + " --- StartOfTransmission");
            try {
                //EAALrRUp2rHEBAH1ZAPIYfzzGHwRNDYLP0KICrOiKOCknfiMjQ2NmCgBb0Ud6QzKSRx2JUXi8YVvrj07ZBbxorZBkCbrQ6usvxMR2BHarZAGToYAPvea1bzHs6vKILL3YIkuNc8xgh23V7i07shswxRHgIPTUz5ftcgVXSY1ZA3gZDZD
                String url = "https://graph.facebook.com/" + FACEBOOK_API_VERSION + "/me/messages";
                url += "?access_token=" + PAGE_ACCESS_TOKEN;
                URLConnection urlc = new URL(url).openConnection();
                ((HttpURLConnection) urlc).setRequestMethod("POST");
                urlc.setDoOutput(true);
                urlc.setRequestProperty("Content-Type", "application/json");
//                urlc.setRequestProperty("access_token", PAGE_ACCESS_TOKEN );

                try (OutputStream output = urlc.getOutputStream()) {
                    output.write(payload.getBytes());
                }

                System.out.println(id + " POST to URL " + url);
                System.out.println(id + " " + payload);

                try {
                    System.out.println(id + " --- Error");
                    System.out.println(id + " " + convertStreamToString(((HttpURLConnection) urlc).getErrorStream()));
                } catch (Exception e) {
                }

                try {
                    System.out.println(id + " --- Input");
                    System.out.println(convertStreamToString(urlc.getInputStream()));
                } catch (Exception e) {
                }

                System.out.println(id + " --- EndOfTransmission");

            } catch (Exception e) {
                System.out.println(id + " --- Exception !!");
                e.printStackTrace();
            }
        }).run();
    }

//    private void addToMap(String id) {
//        Optional<User> u = map.values().stream().filter(o -> o.messenger_id.equals(id)).findAny();
//        if (!u.isPresent())
//            try {
//                String url = "https://graph.facebook.com/" + FACEBOOK_API_VERSION + "/" + id;
//                url += "?fields=first_name,last_name,profile_pic,locale,timezone,gender";
//                url += "&access_token=" + PAGE_ACCESS_TOKEN;
//                System.out.println("URL " + url);
//                HttpURLConnection urlc = (HttpURLConnection) new URL(url).openConnection();
//                urlc.setRequestProperty("Content-Type", "application/json");
//                String json = convertStreamToString(urlc.getInputStream());
//                String err = convertStreamToString(urlc.getErrorStream());
//                System.out.println("URL " + urlc.getResponseCode() + " " + urlc.getResponseMessage());
//
//                if (urlc.getResponseCode() >= 200 && urlc.getResponseCode() < 300) {
//
//                    System.out.println("Response " + json);
//
//                    ObjectMapper mapper = new ObjectMapper();
//                    JsonNode node = mapper.readTree(json);
//
//                    User user = new User();
//                    user.first_name = node.get("first_name").textValue();
//                    user.last_name = node.get("last_name").textValue();
//                    user.gender = node.get("gender").textValue();
//                    user.profile_pic = node.get("profile_pic").textValue();
//                    user.timezone = node.get("timezone").textValue();
//                    user.messenger_id = id;
//
//                    Optional<User> u2 = map.values().stream().filter(
//                            o -> o.last_name.equals(user.last_name) && o.first_name.equals(user.first_name)).findAny();
//                    if (u2.isPresent()) u2.get().messenger_id = id;
//                    else map.put(id, user);
//
///*                FileOutputStream fos = new FileOutputStream(new File("./facebook.obj"));
//                ObjectOutputStream oos = new ObjectOutputStream(fos);
//                oos.writeObject(map);
//                fos.close();*/
//                } else {
//                    try {
//                        System.out.println("Error " + err);
//                    } catch (Exception e) {
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println("\n--- Exception !!");
//                e.printStackTrace();
//            }
//    }

    interface Strategy {
        public void run(JsonNode node);
    }

    //"object":"page","entry":[{"id":"292024547861549","time":1483539426515,"messaging":[{"recipient":{"id":"292024547861549"},"timestamp":1483539426515,"sender":{"id":"1250148388409499"},"optin":{"ref":"PASS_THROUGH_PARAM"}}]}]}
    class Optin implements Strategy {
        public void run(JsonNode node) {
            JsonNode optin = node.get("entry").get(0).get("messaging").get(0).get("optin");
            if (optin != null) {
                String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
                sendTextMessage(id, "Ciao e benvenuto.");
            }
        }
    }

    //{"object":"page","entry":[{"id":"292024547861549","time":1483540183362,"messaging":[{"sender":{"id":"1250148388409499"},"recipient":{"id":"292024547861549"},"timestamp":1483540183207,"message":{"mid":"mid.1483540183207:5b7bfbd757","seq":16121,"text":"azz"}}]}]}
    class Message implements Strategy {
        public void run(JsonNode node) {
            JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
            if (msg != null) {
                String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
                String text = msg.get("text").asText();
                if (text != null)
                    if (text.toLowerCase().contains("is_echo")) {}
                    else
                    if (text.toLowerCase().contains("help")) {
                        new Help().run( node );
                    } else if (text.toLowerCase().contains("fattura")) {
                        sendStructuredMessage(id, "fattura da PINCO PALLINO di " + Math.random() * 500 +"€ ");
                    } else {
                        sendTextMessage(id, "Grazie per la richiesta '" + text + "'");
                    }
            }
        }
    }

    class Help implements Strategy {
        @Override
        public void run(JsonNode node) {
            String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
            sendTextMessage( id, "commands: help, fattura" );
        }
    }

    class PostBack implements Strategy {
        @Override
        public void run(JsonNode node) {
            String payload = node.get("entry").get(0).get("messaging").get(0).get("postback").get("payload").asText();
            System.out.println("Postback -> " + payload );
            int at = payload.indexOf('@');
            if( at>0) {
                String uuid = payload.substring(at+1);
                String act = payload.substring(0, at);
                String text = postbacks.getProperty(uuid);
                System.out.println("Postback -> " + act + " " + uuid + " : " + text);
                postbacks.remove( uuid );

                String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
                sendTextMessage( id, act + ":" + text );
                sendTextMessage( "1253251894751382", "id " + id + " " + act + ":" + text );
            }

        }
    }

    class Read implements Strategy {
        @Override
        public void run(JsonNode node) {
            System.out.println("Read " + node.textValue() );
        }
    }

        @XmlRootElement
    class Userx implements Serializable {
        String messenger_id;
        String id;
        String first_name;
        String last_name;
        String profile_pic;
        String locale;
        String timezone;
        String gender;
        String is_payment_enabled;

        @Override
        public String toString() {
            return "{" +
                    "id=\"" + id + "\"" +
                    ",messenger_id=\"" + messenger_id + "\"" +
                    ", first_name=\"" + first_name + "\"" +
                    ", last_name=\"" + last_name + "\"" +
                    ", profile_pic=\"" + profile_pic + "\"" +
                    ", locale=\"" + locale + "\"" +
                    ", timezone=\"" + timezone + "\"" +
                    ", gender=\"" + gender + "\"" +
                    ", is_payment_enabled=\"" + is_payment_enabled + "\"" +
                    "}";
        }
    }

    @XmlRootElement
    static public class TwoChoicePostback {
        public PBRecipient recipient = new PBRecipient();
        public PBMessage message = new PBMessage();
    }

    static public class PBRecipient {
        public String id;
    }

    static public class PBMessage {
        public PBAttachment attachment = new PBAttachment();
    }

    static public class PBAttachment {
        public String type = "template";
        public PBPayload payload = new PBPayload();
    }

    static public class PBPayload {
        public String template_type = "generic";
        private PBElement last;

        //        public PBElement[] elements = new PBElement[]{ new PBElement() };
        public List<PBElement> elements = new Vector<>();
        public PBElement addElement( String title, String subtitle, String itemurl, String imageurl ){
            addElement(title, subtitle, itemurl).image_url = imageurl;
            return last;
        }

        public PBElement addElement( String title, String subtitle, String itemurl ){
            last= new PBElement();
            last.title=title;
            last.subtitle=subtitle;
            last.item_url=itemurl;
            elements.add(last);
            return last;
        }
        public PBElement last() { return last; }
    }

    static public class PBElement {
        public String title;
        public String subtitle;
        public String item_url;
        public String image_url;
        public List<PBButton> buttons = new Vector<PBButton>();
        public void addWebURLButton( String title, String url ) {
            PBButton uno = new PBButton();
            uno.type = "web_url";
            uno.url = url;
            uno.title = title;
            buttons.add( uno );
        }
        public void addPostbackButton( String title, String payload ) {
            PBButton uno = new PBButton();
            uno.type = "postback";
            uno.title = title;
            uno.payload = payload;
            buttons.add( uno );
        }
    }

    static public class PBButton {
        public String type;
        public String url;
        public String title;
        public String payload;
    }
}