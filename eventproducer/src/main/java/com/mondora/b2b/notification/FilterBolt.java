package com.mondora.b2b.notification;

/**
 * Created by mmondora on 13/01/2017.
 */
import backtype.storm.task.OutputCollector;
        import backtype.storm.task.TopologyContext;
        import backtype.storm.topology.OutputFieldsDeclarer;
        import backtype.storm.tuple.Fields;
        import backtype.storm.tuple.Tuple;
        import backtype.storm.tuple.Values;
        import com.mondora.teamsystem.hub.b2b.event.Status;
        import org.apache.logging.log4j.LogManager;
        import org.apache.logging.log4j.Logger;
        import org.apache.logging.log4j.ThreadContext;

        import java.util.Map;

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
 * 15/06/16
 */
public class FilterBolt extends BaseBolt {
    private transient static final Logger LOG = LogManager.getLogger(FilterBolt.class);

    @Override
    protected void internalPrepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    }

    @Override
    public void execute(Tuple tuple) {
        long now = System.currentTimeMillis();
        ThreadContext.clearAll();
        ThreadContext.put("action", "execute");
        ThreadContext.put("step", "input");
        LOG.info("Filter event");
        Status event = (Status) tuple.getValueByField("status");
        if (event != null && event.isValid() && "SDI".equals(event.getDocType()) && "FIRMATO".equals(event.getEventName())) {
            ThreadContext.put("step", "output");
            ThreadContext.put("exec_time", String.valueOf(System.currentTimeMillis() - now));
            LOG.info("Event emitted: {}", event.toString());
            emit(tuple, new Values(event));
        }
        ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("status"));
    }
}