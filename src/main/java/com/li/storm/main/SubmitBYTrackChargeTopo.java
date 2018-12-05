package com.li.storm.main;


import com.li.common.TopoUtil;
import com.li.storm.bolt.charge.TrackingChargeBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;


public class SubmitBYTrackChargeTopo {
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
		builder.setSpout("mbbi_bpd_tracking_charge_spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 48);
		builder.setBolt("mbbi_bpd_tracking_charge_bolt",new TrackingChargeBolt(),576).setNumTasks(576).shuffleGrouping("mbbi_bpd_tracking_charge_spout");
		try {
			System.setProperty("storm.jar", stormJarPath);
			StormSubmitter.submitTopologyWithProgressBar("bpd_mbbi_tracking_charge",topoUtil.getConfig(100) , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
