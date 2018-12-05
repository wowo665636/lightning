package com.li.storm.topo;

import com.li.common.TopoUtil;
import com.li.storm.bolt.warehouse.TrackBoltDwd;
import com.li.storm.bolt.warehouse.TrackBoltRedis;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;


/**
 * Created by wangdi on 18/7/24.
 * 实时监控
 */

public class TrackWarehouseTopo {
    public static void main(String[] args) {
        TopoUtil topoUtil = new TopoUtil("bpd-track-warehouse-34a");


        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("bpd-track-warehouse-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig("mbbi_bpd_tracking_charge")), 48);
        builder.setBolt("bpd-TrackBoltDwd",new TrackBoltDwd(),360).setNumTasks(360).shuffleGrouping("bpd-track-warehouse-spout");
        builder.setBolt("bpd-TrackBoltRedis",new TrackBoltRedis(),720).setNumTasks(2160).shuffleGrouping("bpd-TrackBoltDwd");
        Config config =  topoUtil.getConfig(200);

        try {
            System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
            StormSubmitter.submitTopologyWithProgressBar("bpd-track-warehouse", config , builder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
