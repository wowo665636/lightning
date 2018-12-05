package com.li.storm.topo;

import com.li.common.TopoUtil;
import com.li.storm.bolt.sstics.XpsSearchBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wangdi on 18/4/2.
 */
public class XpsSearchTopo {

    public static void main(String[] args) {
        TopoUtil topoUtil = new TopoUtil("bpd-xpssearch-topo-3f");
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("bpd-xpssearch-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig("xpssearch")), 36);
        builder.setBolt("bpd-xpssearch-bolt",new XpsSearchBolt(),360).setNumTasks(720).shuffleGrouping("bpd-xpssearch-spout");
        try {
            System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
           StormSubmitter.submitTopologyWithProgressBar("bpd-xpssearch-topo",topoUtil.getConfig(50) , builder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
