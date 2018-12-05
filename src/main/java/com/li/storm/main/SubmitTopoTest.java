package com.li.storm.main;

import com.li.common.TopoUtil;
import com.li.storm.bolt.pt30.TrackPt30Bolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wangdi on 18/11/29.
 */
public class SubmitTopoTest {
    public static void main(String[] args) {
        if(args.length<2){
            System.out.println("请输入正确参数:");
            System.out.println("[参数1]:topic name");
            System.out.println("[参数2]:group id");
            System.out.println("[参数3]:storm jar path");
            System.exit(0);
            return ;
        }
        String topicName=args[0];//mbbi_bpd_tracking_charge
        String groupId = args[1];//bpd-track-pt30-3a
        String stormJarPath =  args[2];//System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar"
        TopoUtil topoUtil = new TopoUtil(groupId);
        final TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("bpd-test-spout",new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 48);
        builder.setBolt("bpd-test-bolt",new TestBolt(),48).setNumTasks(48).shuffleGrouping("bpd-test-spout");
        try {
            System.setProperty("storm.jar",stormJarPath );
            StormSubmitter.submitTopologyWithProgressBar("bpd-test-topo",topoUtil.getConfig(5) , builder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
