package com.mondora;

import com.mondora.facebook.Connector;
import com.mondora.facebook.PusherThread;
import com.mondora.facebook.menu.Menu;
import com.mondora.model.FBUser;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@SpringBootApplication
@EnableJms
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        Objects.requireNonNull(System.getenv("BUS_USER"), "BUS_USER must not be null");
        Objects.requireNonNull(System.getenv("BUS_PASS"), "BUS_PASS must not be null");
        Objects.requireNonNull(System.getenv("BUS_HOST"), "BUS_HOST must not be null");
        Objects.requireNonNull(System.getenv("TOPIC_NAME"), "TOPIC_NAME must not be null");
        Objects.requireNonNull(System.getenv("SUBSCRIPTION_NAME"), "SUBSCRIPTION_NAME must not be null");
        Objects.requireNonNull(System.getenv("SUBSCRIPTION_NAME"), "SUBSCRIPTION_NAME must not be null");
        Objects.requireNonNull(System.getenv("FACEBOOK_PAGE_ACCESS_TOKEN"), "FACEBOOK_PAGE_ACCESS_TOKEN must not be null");

        Database.loadMap("users.obj");

        SpringApplication.run(Application.class, args);

        LOG.info("Creazioni Fatture ");
        int day = 86400000;
        if (Database.users.size() > 0)
            for (int i = 0; i < Database.users.size() * 5; i++) {
                Optional<FBUser> r = Database.randomUser();
                if (r.isPresent() && r.get().b2b_id != null) {
                    Database.addFattura(
                            new Fattura(
                                    new Date(System.currentTimeMillis() + (i * day) * (i % 2 == 0 ? -1 : 1)),
                                    Database.randomCustomer(),
                                    Math.random() * 500,
                                    r.get().b2b_id
                            )
                    );
                }
            }


        LOG.info("Avviso utenti");
        Collection<FBUser> users = Database.listAllUsers();
        if (users != null)
            users.forEach(u -> {
                Connector.sendTextMessage(u.messenger_id, "Agyo bot is back.");
            });

        new com.mondora.facebook.commands.Menu().run(null);
        new Thread(new PusherThread()).start();
    }

    @PreDestroy
    public void shutdown() {
        Database.saveMap("users.obj");

        Collection<FBUser> users = Database.listAllUsers();
        users.forEach(u -> {
            Connector.sendTextMessage(u.messenger_id, "Agyo bot is shutting down.");
        });
    }

}