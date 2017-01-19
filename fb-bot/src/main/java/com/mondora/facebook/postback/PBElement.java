package com.mondora.facebook.postback;

import java.util.List;
import java.util.Vector;

/**
 * Created by mmondora on 12/01/2017.
 */
public class PBElement {
    public String title;
    public String subtitle;
    public String item_url;
    public String image_url;
    public List<PBButton> buttons = new Vector<PBButton>();

    public void addWebURLButton(String title, String url) {
        PBButton uno = new PBButton();
        uno.type = "web_url";
        uno.url = url;
        uno.title = title;
        buttons.add(uno);
    }

    public void addPostbackButton(String title, String payload) {
        PBButton uno = new PBButton();
        uno.type = "postback";
        uno.title = title;
        uno.payload = payload;
        buttons.add(uno);
    }
}
