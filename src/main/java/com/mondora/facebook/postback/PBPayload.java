package com.mondora.facebook.postback;

import java.util.List;
import java.util.Vector;

/**
 * Created by mmondora on 12/01/2017.
 */
public class PBPayload {
    public String template_type = "generic";
    //        public PBElement[] elements = new PBElement[]{ new PBElement() };
    public List<PBElement> elements = new Vector<>();
    private PBElement last;

    public PBElement addElement(String title, String subtitle, String itemurl, String imageurl) {
        addElement(title, subtitle, itemurl).image_url = imageurl;
        return last;
    }

    public PBElement addElement(String title, String subtitle, String itemurl) {
        last = new PBElement();
        last.title = title;
        last.subtitle = subtitle;
        last.item_url = itemurl;
        elements.add(last);
        return last;
    }

    public PBElement last() {
        return last;
    }
}
