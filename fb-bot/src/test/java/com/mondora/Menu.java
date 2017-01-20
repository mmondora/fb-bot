package com.mondora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mmondora on 20/01/2017.
 */
public class Menu {

    @Test
    public void testMenu() {
        String expected = "{\n" +
                " \"setting_type\" : \"call_to_actions\",\n" +
                " \"thread_state\" : \"existing_thread\",\n" +
                " \"call_to_actions\":[\n" +
                " {\n" +
                " \"type\":\"postback\",\n" +
                " \"title\":\"Help\",\n" +
                " \"payload\":\"DEVELOPER_DEFINED_PAYLOAD_FOR_HELP\"\n" +
                " },\n" +
                " {\n" +
                " \"type\":\"postback\",\n" +
                " \"title\":\"Start a New Order\",\n" +
                " \"payload\":\"DEVELOPER_DEFINED_PAYLOAD_FOR_START_ORDER\"\n" +
                " },\n" +
                " {\n" +
                " \"type\":\"web_url\",\n" +
                " \"title\":\"Checkout\",\n" +
                " \"url\":\"http://petersapparel.parseapp.com/checkout\",\n" +
                " \"webview_height_ratio\": \"full\",\n" +
                " \"messenger_extensions\": true\n" +
                " },\n" +
                " {\n" +
                " \"type\":\"web_url\",\n" +
                " \"title\":\"View Website\",\n" +
                " \"url\":\"http://petersapparel.parseapp.com/\"\n" +
                " }\n" +
                " ]\n" +
                " }";


        com.mondora.facebook.menu.Menu m = new com.mondora.facebook.menu.Menu();
        m.addPostBack( "Help", "DEVELOPER_DEFINED_PAYLOAD_FOR_HELP" );
        m.addPostBack( "Start a New Order", "DEVELOPER_DEFINED_PAYLOAD_FOR_START_ORDER" );
        m.addWebURL("Checkout", "http://petersapparel.parseapp.com/checkout");
        m.addWebURL("View Website", "http://petersapparel.parseapp.com/");

        String json = Utils.toJson( m );
        System.out.println( "json\n" + json );

//        assertEquals( expected, json );
    }
}
