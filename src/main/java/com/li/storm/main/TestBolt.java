package com.li.storm.main;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by wangdi on 18/11/29.
 */
public class TestBolt  implements IRichBolt {
    private static final long serialVersionUID = -8592384984225861335L;
    private OutputCollector collector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String message = (String)tuple.getValue(4);
        System.out.println(message);
        collector.ack(tuple);

    }

    @Override
    public void cleanup() {
        System.out.println("TestBolt is close");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
