package com.mondora.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Fattura {
    public Date data;
    public double importo;
    public String mittente;
    public String id;

    public Fattura(String data, String mittente, double importo ) {
        this.id = UUID.randomUUID().toString();
        try {
            this.data = formatter.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
            this.data = new Date();
        }
        this.importo = importo;
        this.mittente = mittente;
    }

    static SimpleDateFormat formatter = new SimpleDateFormat( "dd-MM-yy");

}
