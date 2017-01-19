package com.mondora;

import com.mondora.model.FBUser;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mmondora on 12/01/2017.
 */

public class DbTest {
    @Test
    public void testSave() {
        FBUser u = new FBUser();
        u.first_name = "Michele";
        u.last_name = "Mondora";
        u.messenger_id = "1253251894751382";

        Database.saveUser("1253251894751382", u );
        Database.saveMap("test.obj");

        Database.clearUsers();
        Map<String,FBUser> load = Database.loadMap("test.obj" );
        assertNotNull( load );
        assertEquals(1,load.size());

        load.forEach( (o,v)->System.out.println( o + " " + v));
    }
}
