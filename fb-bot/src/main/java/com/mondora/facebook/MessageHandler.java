package com.mondora.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.Utils;
import com.mondora.facebook.commands.Default;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */ //{"object":"page","entry":[{"id":"292024547861549","time":1483540183362,"messaging":[{"sender":{"id":"1250148388409499"},"recipient":{"id":"292024547861549"},"timestamp":1483540183207,"message":{"mid":"mid.1483540183207:5b7bfbd757","seq":16121,"text":"azz"}}]}]}
public class MessageHandler extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);
    public void run(JsonNode node) {
        LOG.debug("run Message " + Utils.toJson(node));
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        if (msg != null && msg.get("text") != null ) {
            String id = getId( node );
            FBUser u = Database.findUser(id);
            String text = msg.get("text").asText();
            Strategy s = StrategyBuilder.buildStrategyFromMessage(text);
            if( s != null ) s.run(node);
        }
    }
}

/*
run Message {
  "object" : "page",
  "entry" : [ {
    "id" : "387095934984311",
    "time" : 1484856494437,
    "messaging" : [ {
      "sender" : {
        "id" : "1493910707316146"
      },
      "recipient" : {
        "id" : "387095934984311"
      },
      "timestamp" : 1484856469580,
      "message" : {
        "mid" : "mid.1484856469580:aa32fcc332",
        "seq" : 74922,
        "sticker_id" : 369239263222822,
        "attachments" : [ {
          "type" : "image",
          "payload" : {
            "url" : "https://scontent.xx.fbcdn.net/t39.1997-6/851557_369239266556155_759568595_n.png?_nc_ad=z-m",
            "sticker_id" : 369239263222822
          }
        } ]
      }
    } ]
  } ]
}

 */
