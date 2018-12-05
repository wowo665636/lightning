package com.li.storm.topo;


import com.li.common.TopoUtil;
import com.li.storm.bolt.nocharge.TrackNoCharge2RedisBolt;
import com.li.storm.bolt.nocharge.TrackNoChargeEtlBot;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;


public class TrackNoChargeTopo {
	public static void main(String[] args) {

		TopoUtil topoUtil = new TopoUtil("bpd-track-nocharge-3a");
		final TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("bpd-nocharge-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig("xpstrackingnocharge")), 3);
		builder.setBolt("bpd-trackNoChargeEtlBot-bolt",new TrackNoChargeEtlBot(),30).setNumTasks(30).shuffleGrouping("bpd-nocharge-spout");
		builder.setBolt("bpd-TrackNoCharge2RedisBolt-bolt", new TrackNoCharge2RedisBolt(),60).setNumTasks(60).shuffleGrouping("bpd-trackNoChargeEtlBot-bolt");
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-track-nocharge",topoUtil.getConfig(70) , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}




	}

}
