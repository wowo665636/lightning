package com.li.storm.main;

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

public class SubmitTrackWarehouseTopo {
    public static void main(String[] args) {
        if(args.length<2){
            System.out.println("请输入正确参数:");
            System.out.println("[参数1]:topic name");
            System.out.println("[参数2]:group id");
            System.out.println("[参数3]:storm jar path");
            System.exit(0);
            return ;
        }
        String topicName=args[0];
        String groupId = args[1];
        String stormJarPath =  args[2];
        TopoUtil topoUtil = new TopoUtil(groupId);
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("bpd-track-warehouse-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 48);
        builder.setBolt("bpd-TrackBoltDwd",new TrackBoltDwd(),360).setNumTasks(360).shuffleGrouping("bpd-track-warehouse-spout");
        builder.setBolt("bpd-TrackBoltRedis",new TrackBoltRedis(),720).setNumTasks(2160).shuffleGrouping("bpd-TrackBoltDwd");
        Config config =  topoUtil.getConfig(200);

        try {
            System.setProperty("storm.jar", stormJarPath);
            StormSubmitter.submitTopologyWithProgressBar("bpd-track-warehouse", config , builder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
