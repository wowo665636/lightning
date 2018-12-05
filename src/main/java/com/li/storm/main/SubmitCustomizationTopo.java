package com.li.storm.main;


import com.li.common.TopoUtil;
import com.li.storm.bolt.sstics.CustomizationMonitorBolt;
import com.li.storm.bolt.sstics.DimBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 *  定制化topo
 */
public class SubmitCustomizationTopo {
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
		/************* media data ***20180413************/
		builder.setSpout("bpd-customization-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 48);
		builder.setBolt("bpd-customization-bolt", new CustomizationMonitorBolt(), 360)
				.setNumTasks(720).shuffleGrouping("bpd-customization-spout");
		builder.setBolt("bpd-dim-bolt", new DimBolt(), 1440).setNumTasks(2880)
				.shuffleGrouping("bpd-customization-bolt");

		try {
			System.setProperty("storm.jar", stormJarPath);
			StormSubmitter.submitTopologyWithProgressBar("bpd-track-customization",
					topoUtil.getConfig(200), builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
