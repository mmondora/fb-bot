package com.mondora.b2b.notification;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import com.mondora.teamsystem.hub.storm.BaseEventHubTopology;
import org.apache.storm.eventhubs.spout.EventHubSpout;

/**
 * Created by mmondora on 13/01/2017.
 */

/**
 * |__________________________________________________________
 * |   _                      __                              |
 * |  (_) __ _  ___  ___  ___/ /__  _______ _ _______  __ _   |
 * |     /  ' \/ _ \/ _ \/ _  / _ \/ __/ _ `// __/ _ \/  ' \  |
 * |(_) /_/_/_/\___/_//_/\_,_/\___/_/  \_,_(_)__/\___/_/_/_/  |
 * |                 - computing essence -                    |
 * |__________________________________________________________|
 * |
 * Author Davide Pedone <davide.pedone@mondora.com>
 * 27/05/16
 */
public class NotificationTopology extends BaseEventHubTopology {

    //TODO check & fix naming of output fields
    @Override
    protected StormTopology buildTopology(EventHubSpout eventHubSpout) {
        int partitionCount = getSpoutConfig().getPartitionCount();
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        topologyBuilder.setSpout("all-events", eventHubSpout, partitionCount)
                .setNumTasks(partitionCount);
        topologyBuilder.setBolt("filter-bolt", new FilterBolt(), 2)
                .localOrShuffleGrouping("all-events")
                .setNumTasks(2);
//
//        topologyBuilder.setBolt("validation-filter-bolt", new ValidationFilterBolt(), 2)
//                .localOrShuffleGrouping("parse-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("check-subscription-bolt", new CheckSDISubscriptionBolt(), 2)
//                .localOrShuffleGrouping("validation-filter-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("validation-bolt", new ValidationBolt(), 2)
//                .localOrShuffleGrouping("check-subscription-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("prepareFile-bolt", new PrepareFileBolt(), 2)
//                .localOrShuffleGrouping("validation-bolt")
//                .setNumTasks(2);
//
//        topologyBuilder.setBolt("upload-filter-bolt", new UploadFilterBolt(), 2)
//                .localOrShuffleGrouping("parse-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("upload-bolt", new UploadBolt(), 2)
//                .localOrShuffleGrouping("upload-filter-bolt")
//                .setNumTasks(2);
//
//        topologyBuilder.setBolt("parse-notification-bolt", new ParseNotificationBolt(), 2)
//                .localOrShuffleGrouping("all-events")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("notification-filter-bolt", new NotificationFilterBolt(), 2)
//                .localOrShuffleGrouping("parse-notification-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("notification-bolt", new NotificationBolt(), 2)
//                .localOrShuffleGrouping("notification-filter-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("process-notification-bolt", new ProcessNotificationBolt(), 2)
//                .localOrShuffleGrouping("notification-bolt")
//                .setNumTasks(2);
//
//        // SdI passivo
//        /*
//            1) Recupera il file, e guarda se è uno zip o xml
//            2) se xml guarda se più fatture all'interno
//            3) per ogni fattura chiamo le API dell'hub e faccio l'upload.
//            4) il punto 3 mi torna un hub-id, che scrivo su una tabella con associato lo sdi-id che mi arriva dal ws e il numero di fattura che mi tengo
//            in canna dal punto 2.
//        */
//        topologyBuilder.setBolt("parse-passive-bolt", new ParsePassiveEventBolt(), 2)
//                .localOrShuffleGrouping("all-events")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("process-file-bolt", new ProcessFileBolt(), 2)
//                .localOrShuffleGrouping("parse-passive-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("upload-invoice-bolt", new UploadInvoiceBolt(), 2)
//                .localOrShuffleGrouping("process-file-bolt")
//                .setNumTasks(2);
//
//        /*
//            La macchina a stati non può generare 2 eventi con lo stesso hash, quindi intercetto
//            l'evento "CONTROLLATO" e genero io l'evento "A_DISPOSIZIONE"
//         */
//        topologyBuilder.setBolt("checked-filter-bolt", new CheckedFilterBolt(), 2)
//                .localOrShuffleGrouping("parse-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("checked-bolt", new CheckedBolt(), 2)
//                .localOrShuffleGrouping("checked-filter-bolt")
//                .setNumTasks(2);
//
//        /*
//
//        In accettato o rifiutato ci vado solo se arrivo dallo stato RICEVUTO (aggiornare la macchina a stati!!!!!)
//
//        Un'altra bolt che si aggancia a all events e intercetta gli eventi di accettato o rifiutato + passivesdi
//        chiamo la webapp che chiama lo sdi con la notifica di accettato o rifiutato
//        nel riferimento fattura della notifica ci devo mettere:
//
//        RiferimentoFattura Opzionale. Descrive a quale fattura si riferisce l’esito;
//        se non valorizzato si intende riferito a tutte le fatture presenti nel file --> numero fattura che sta in body->dati generali->
//        dati generali doc-> numero
//
//        + aggiunere motivazione che prendo da status.description
//
//        Se la chiamata alla webapp va a buon fine aggiorno lo stato in ACCETTATO o RIFIUTATO
//         */
//        topologyBuilder.setBolt("received-filter-bolt", new ReceivedFilterBolt(), 2)
//                .localOrShuffleGrouping("parse-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("received-bolt", new ReceivedBolt(), 2)
//                .localOrShuffleGrouping("received-filter-bolt")
//                .setNumTasks(2);
//
//        /*
//        Intercetto gli eventi PASSIVESDI_DT: per questi vado con lo sdiId sul db e recupero tutti gli hubid
//        per ognuno mando l'evento NESSUNA_RISPOSTA
//         */
//        topologyBuilder.setBolt("parse-noanswer-bolt", new ParseNoAnswerBolt(), 2)
//                .localOrShuffleGrouping("all-events")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("noanswer-filter-bolt", new NoAnswerFilterBolt(), 2)
//                .localOrShuffleGrouping("parse-noanswer-bolt")
//                .setNumTasks(2);
//        topologyBuilder.setBolt("noanswer-bolt", new NoAnswerBolt(), 2)
//                .localOrShuffleGrouping("noanswer-filter-bolt")
//                .setNumTasks(2);

        return topologyBuilder.createTopology();
    }

    public static void main(String[] args) throws Exception {
        NotificationTopology sdiTopology = new NotificationTopology();
        sdiTopology.runScenario(args);
    }

}