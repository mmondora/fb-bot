package com.mondora.facebook.menu;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 * Created by mmondora on 20/01/2017.
 *
 * {
 "setting_type" : "call_to_actions",
 "thread_state" : "existing_thread",
 "call_to_actions":[
 {
 "type":"postback",
 "title":"Help",
 "payload":"DEVELOPER_DEFINED_PAYLOAD_FOR_HELP"
 },
 {
 "type":"postback",
 "title":"Start a New Order",
 "payload":"DEVELOPER_DEFINED_PAYLOAD_FOR_START_ORDER"
 },
 {
 "type":"web_url",
 "title":"Checkout",
 "url":"http://petersapparel.parseapp.com/checkout",
 "webview_height_ratio": "full",
 "messenger_extensions": true
 },
 {
 "type":"web_url",
 "title":"View Website",
 "url":"http://petersapparel.parseapp.com/"
 }
 ]
 }
 */

@XmlRootElement
public class Menu {
    public String setting_type = "call_to_actions";
    public String thread_state = "existing_thread";
    public List<Action> call_to_actions = new Vector<>();

    public Action addWebURL(String title, String url) {
        WebURL a = new WebURL();
        a.type = "web_url";
        a.title = title;
        a.url = url;
        call_to_actions.add( a );
        return a;
    }

    public Action addPostBack( String title, String payload ) {
        Action a = new Action();
        a.type = "postback";
        a.title = title;
        a.payload = payload;
        call_to_actions.add( a );
        return a;
    }
}



