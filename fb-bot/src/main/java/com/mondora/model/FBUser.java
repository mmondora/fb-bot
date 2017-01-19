package com.mondora.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by mmondora on 12/01/2017.
 */
@XmlRootElement
public class FBUser implements Serializable {
    public String messenger_id;
    public String id;
    public String first_name;
    public String last_name;
    public String profile_pic;
    public String locale;
    public String timezone;
    public String gender;
    public String is_payment_enabled;
    public String b2b_id;

    @Override
    public String toString() {
        return "{" +
                " \"id\" : \"" + id + "\", " +
                " \"messenger_id\" : \"" + messenger_id + "\"" +
                ", \"first_name\" :\"" + first_name + "\"" +
                ", \"last_name\" :\"" + last_name + "\"" +
                ", \"b2b_id\" :\"" + b2b_id + "\"" +
//                ", \"profile_pic\" :\"" + profile_pic + "\"" +
//                ", \"locale\" : \"" + locale + "\"" +
//                ", \"timezone\" :\"" + timezone + "\"" +
//                ", \"gender\" :\"" + gender + "\"" +
//                ", \"is_payment_enabled\":\"" + is_payment_enabled + "\"" +
                "}";
    }
}