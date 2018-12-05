package com.li.storm.topo;

import com.li.common.TopoUtil;
import com.li.storm.bolt.sstics.TvParseBolt;
import com.li.storm.bolt.sstics.TvRedisBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wangdi on 18/7/4.
 */
public class TvFrameTopo {

    public static void main(String[] args) {
        String topic_id="newtvadpv"; //tvadmpv

        TopoUtil topoUtil = new TopoUtil("bpd-"+topic_id+"-b2");
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("bpd-tv-spout",new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topic_id)),8);
        builder.setBolt("TvParseBolt",new TvParseBolt(),8).setNumTasks(16).shuffleGrouping("bpd-tv-spout");
        builder.setBolt("TvRedisBolt",new TvRedisBolt(),8).setNumTasks(16).shuffleGrouping("TvParseBolt");

        try{
            System.setProperty("storm.jar",System.getProperty("user.dir")+ "/target/lightning-jar-with-dependencies.jar");
            StormSubmitter.submitTopologyWithProgressBar("bpd-"+topic_id+"-topo",topoUtil.getConfig(10),builder.createTopology());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
