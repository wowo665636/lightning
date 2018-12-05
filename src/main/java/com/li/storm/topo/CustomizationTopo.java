package com.li.storm.topo;


import com.li.common.TopoUtil;
import com.li.storm.bolt.sstics.CustomizationMonitorBolt;
import com.li.storm.bolt.sstics.DimBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 *  定制化topo
 */
public class CustomizationTopo {
	public static void main(String[] args) {

		TopoUtil topoUtil = new TopoUtil("bpd-track-customization-2c");
		final TopologyBuilder builder = new TopologyBuilder();
		/************* media data ***20180413************/
		builder.setSpout("bpd-customization-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig("mbbi_bpd_tracking_charge")), 48);
		builder.setBolt("bpd-customization-bolt", new CustomizationMonitorBolt(), 360)
				.setNumTasks(720).shuffleGrouping("bpd-customization-spout");
		builder.setBolt("bpd-dim-bolt", new DimBolt(), 1440).setNumTasks(2880)
				.shuffleGrouping("bpd-customization-bolt");

		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-track-customization",
					topoUtil.getConfig(200), builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
