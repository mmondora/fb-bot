package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.commands.Default;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mmondora on 19/01/2017.
 */
public class FatturaNotification extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Default.class);
    static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00€");

    @Override
    public void run(JsonNode node) {
        String id = getId( node );
        FBUser fb = Database.findUser( id );
        if( fb.b2b_id != null ) {
            Fattura f = new Fattura( new Date(), Database.randomCustomer(), Math.random() * 1000, fb.b2b_id );
            Database.addFattura( f );
            String fattua = "Ricevuta fattura da " + f.mittente  + " di " + decimalFormat.format( f.importo ) + "€ ";
            LOG.info("Nuova " + fattua);
            String uuid = UUID.randomUUID().toString();
            sendStructuredMessage(id, uuid, fattua);
            Database.addPostbacks(uuid, fattua);
        } else {
            sendTextMessage(id, "Devi fornire le credenziali b2b.");
        }
    }
}
