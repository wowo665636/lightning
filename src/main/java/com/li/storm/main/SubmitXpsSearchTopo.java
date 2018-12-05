package com.li.storm.main;

import com.li.common.TopoUtil;
import com.li.storm.bolt.sstics.XpsSearchBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by wangdi on 18/4/2.
 */
public class SubmitXpsSearchTopo {


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
        builder.setSpout("bpd-xpssearch-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 60);
        builder.setBolt("bpd-xpssearch-bolt",new XpsSearchBolt(),360).setNumTasks(720).shuffleGrouping("bpd-xpssearch-spout");
        try {
            System.setProperty("storm.jar",stormJarPath);
           StormSubmitter.submitTopologyWithProgressBar("bpd-xpssearch-topo",topoUtil.getConfig(50) , builder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
