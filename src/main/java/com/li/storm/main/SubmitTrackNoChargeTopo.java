package com.li.storm.main;


import com.li.common.TopoUtil;
import com.li.storm.bolt.nocharge.TrackNoCharge2RedisBolt;
import com.li.storm.bolt.nocharge.TrackNoChargeEtlBot;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;


public class SubmitTrackNoChargeTopo {
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
		builder.setSpout("bpd-nocharge-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 3);
		builder.setBolt("bpd-trackNoChargeEtlBot-bolt",new TrackNoChargeEtlBot(),30).setNumTasks(30).shuffleGrouping("bpd-nocharge-spout");
		builder.setBolt("bpd-TrackNoCharge2RedisBolt-bolt", new TrackNoCharge2RedisBolt(),60).setNumTasks(60).shuffleGrouping("bpd-trackNoChargeEtlBot-bolt");
		try {
			System.setProperty("storm.jar",stormJarPath);
			StormSubmitter.submitTopologyWithProgressBar("bpd-track-nocharge",topoUtil.getConfig(70) , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}




	}

}
