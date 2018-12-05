package com.li.storm.main;

import com.li.common.TopoUtil;
import com.li.storm.bolt.pt30.TrackPt30Bolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wangdi on 18/10/25.
 */
public class SubmitTrackPt30Topo {
    public static void main(String[] args) {
        if(args.length<2){
            System.out.println("请输入正确参数:");
            System.out.println("[参数1]:topic name");
            System.out.println("[参数2]:group id");
            System.out.println("[参数3]:storm jar path");
            System.exit(0);
            return ;
        }
        String topicName=args[0].trim();//mbbi_bpd_tracking_charge
        String groupId = args[1].trim();//bpd-track-pt30-3a
        String stormJarPath =  args[2].trim();//System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar"
        TopoUtil topoUtil = new TopoUtil(groupId);
        final TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("bpd-pt30-spout",new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 48);
        builder.setBolt("bpd-pt30-bolt",new TrackPt30Bolt(),450).setNumTasks(450).shuffleGrouping("bpd-pt30-spout");
        try {
            System.setProperty("storm.jar", stormJarPath );
            StormSubmitter.submitTopologyWithProgressBar("bpd-track-pt30",topoUtil.getConfig(30) , builder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
