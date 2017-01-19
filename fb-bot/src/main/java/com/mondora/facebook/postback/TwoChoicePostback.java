package com.mondora.facebook.postback;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mmondora on 12/01/2017.
 */
@XmlRootElement
public class TwoChoicePostback {
    public PBRecipient recipient = new PBRecipient();
    public PBMessage message = new PBMessage();
}
