package com.mondora;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

@Controller
@RequestMapping("/")
public class HelloController {

    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public HelloController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
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
        sentInstantMessage(facebook.userOperations().getUserProfile().getId());
        return "hello";
    }


    public void sentInstantMessage(String id) {
        try {                         //EAALrRUp2rHEBAH1ZAPIYfzzGHwRNDYLP0KICrOiKOCknfiMjQ2NmCgBb0Ud6QzKSRx2JUXi8YVvrj07ZBbxorZBkCbrQ6usvxMR2BHarZAGToYAPvea1bzHs6vKILL3YIkuNc8xgh23V7i07shswxRHgIPTUz5ftcgVXSY1ZA3gZDZD
            String PAGE_ACCESS_TOKEN = "EAALrRUp2rHEBAFZC728a8s7EKXZA80vGw5pzvMyWYyZB0O7Mp2TRBvTZAZCRa0L66fdUEZBTzmrL4Pw3N3mVLOq3TzEmGmRgt2gRsvRqY97vHMmIpOIMjTJZBEuZAt0BTAZB4RZCn1zJ7e42qZC93Q3u535sh3Wssf5teqIhz0ywfcZBCwZDZD";
            String url = "https://graph.facebook.com/v2.6/me/messages";
            URLConnection urlc = new URL(url).openConnection();
            ((HttpURLConnection) urlc).setRequestMethod("POST");
            urlc.setDoOutput(true);
            urlc.setRequestProperty("access_token", PAGE_ACCESS_TOKEN);

            String text = "Hello from HelloController " + new Date();
            String payload = "{" +
                    "recipient: { id: '" + id + "'}," +
                    "message: { text: '" + text + "'  }," +
                    "notification_type: 'REGULAR' " +
                    "}";
            try (OutputStream output = urlc.getOutputStream()) {
                output.write(payload.getBytes());
            }

            System.out.println("Fatto. Mandato. " + PAGE_ACCESS_TOKEN );
            System.out.println( payload );

            System.out.println("\nInput");
            System.out.println( convertStreamToString(urlc.getInputStream() ));
            System.out.println("\nError");
            System.out.println(convertStreamToString(((HttpURLConnection) urlc).getErrorStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Verso la fine.");
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}