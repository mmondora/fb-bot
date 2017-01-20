package com.mondora.facebook.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Utils;
import com.mondora.facebook.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 20/01/2017.
 */
public class Menu extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Menu.class);

    public void run(JsonNode node) {
        com.mondora.facebook.menu.Menu m = new com.mondora.facebook.menu.Menu();
        m.addPostBack( "Aiuto", "help" );
        m.addPostBack( "Fatture di oggi", "listToday" );
        m.addPostBack( "Statistiche", "stats" );
        m.addWebURL("Console agyo.io", "https://app.agyo.io/console/index.html");

        String json = Utils.toJson( m );
        LOG.debug("Menu {}", json );
        POST( "https://graph.facebook.com/"+Connector.FACEBOOK_API_VERSION+"/me/thread_settings", json );
        LOG.info( "Menu installed." );
    }
}
