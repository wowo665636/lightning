package com.li.storm.topo;


import com.li.common.TopoUtil;
import com.li.storm.bolt.charge.TrackingChargeBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;


public class BYTrackChargeTopo {
	public static void main(String[] args) {


		TopoUtil topoUtil = new TopoUtil("mbbi_bpd_tracking_charge-2a");
		final TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("mbbi_bpd_tracking_charge_spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig("mbbi_bpd_tracking_charge")), 48);
		builder.setBolt("mbbi_bpd_tracking_charge_bolt",new TrackingChargeBolt(),576).setNumTasks(576).shuffleGrouping("mbbi_bpd_tracking_charge_spout");
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd_mbbi_tracking_charge",topoUtil.getConfig(100) , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
