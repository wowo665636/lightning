package com.li.storm.topo;

import com.li.common.TopoUtil;
import com.li.storm.bolt.pt30.TrackPt30Bolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wangdi on 18/10/25.
 */
public class TrackPt30Topo {
    public static void main(String[] args) {
        TopoUtil topoUtil = new TopoUtil("bpd-track-pt30-6a");
        final TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("bpd-pt30-spout",new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig("mbbi_bpd_tracking_charge")), 90);
        builder.setBolt("bpd-pt30-bolt",new TrackPt30Bolt(),450).setNumTasks(450).shuffleGrouping("bpd-pt30-spout");
        try {
            System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
            StormSubmitter.submitTopologyWithProgressBar("bpd-track-pt30",topoUtil.getConfig(30) , builder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
