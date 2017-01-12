package com.mondora;

import com.mondora.model.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);
    static final Map<String, FBUser> users = new HashMap<>();
    static final Properties postbacks = new Properties();

    static {
        loadMap("users.obj");
    }

    public static void saveMap(String filename) {
        if (users != null && !users.isEmpty())
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saving Map for users");
                    users.forEach((o, i) -> LOG.debug(i.toString()));
                }
                final OutputStream out = new FileOutputStream(new File(filename));
                try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
                    oos.writeObject(users);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    protected static Map<String, FBUser> loadMap(String filename) {
        try {
            final InputStream out = new FileInputStream(new File(filename));
            try (ObjectInputStream oos = new ObjectInputStream(out)) {
                Map<String, FBUser> z = (HashMap<String, FBUser>) oos.readObject();
                users.putAll(z);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    protected static Collection<FBUser> listAll() {
        return users.values();
    }

    public static void addPostbacks(String uuid, String text) {
        postbacks.put(uuid,text);
    }

    public static String getPostback(String uuid) {
        return postbacks.getProperty( uuid );
    }

    public static void removePostback(String uuid) {
        postbacks.remove(uuid);
    }

    public static FBUser findUser(String id) {
        return users.get(id);
    }

    public static void saveUser(String id, FBUser user) {
        users.put( id, user );
    }

    public static void clearUsers() {
        users.clear();
    }

    public static Collection<String> listAllPostback() {
        Vector<String> out = new Vector<>();
        if( ! postbacks.isEmpty() )
            postbacks.stringPropertyNames().forEach(o -> out.add("{ \"id\" : \"" + o + "\", \"value\" : \"" + postbacks.getProperty(o)+"\"}"));
        return out;
    }
}
