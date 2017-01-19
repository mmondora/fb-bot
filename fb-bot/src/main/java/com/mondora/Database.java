package com.mondora;

import com.mondora.model.FBUser;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);
    static final Map<String, FBUser> users = new HashMap<>();
    static final Properties postbacks = new Properties();
    static final Map<String,Fattura> fattura = new HashMap();

    static {
        loadMap("users.obj");

        addFattura( new Fattura( "15-01-2017", "QC Terme Bormio", 150.27 ));
        addFattura( new Fattura( "18-01-2017", "Mondora SRL SB", 10 ));
        addFattura( new Fattura( "19-01-2017", "Easy Rent SRL", 209.51 ));
        addFattura( new Fattura( "18-01-2017", "Autogrill S.P.A.", 55.3 ));
        addFattura( new Fattura( "22-01-2017", "Cozza Amara", 200 ));
        addFattura( new Fattura( "23-01-2017", "Mercedes Benz", 7500 ));
        addFattura( new Fattura( "26-01-2017", "TIM s.p.a.", 25 ));
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
    public static Optional<FBUser> findUserByB2B(String id) {
        return users.values().stream().filter( o->o.b2b_id.equals(id )).findAny();
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
            postbacks.stringPropertyNames().forEach(o -> out.add("{ \"id\" : \"" + o + "\", \"value\" : \"" + postbacks.getProperty(o) + "\"}"));
        return out;
    }

    public static Collection<Fattura> listFattura() {
        return fattura.values();
    }

    public static void addFattura( Fattura f ) {
        fattura.put( f.id, f );
    }
    public static double totaleFattura() {
        return fattura.values().stream().mapToDouble( o->o.importo ).sum();
    }
}
