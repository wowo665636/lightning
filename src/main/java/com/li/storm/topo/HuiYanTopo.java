package com.li.storm.topo;


import com.li.common.TopoUtil;
import com.li.storm.bolt.huiyan.LogDealBolt;
import com.li.storm.bolt.huiyan.LogParseBolt;
import com.li.storm.bolt.huiyan.RedisBolt;
import com.li.util.PropertiesLoader;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 *  定制化topo
 */
public class HuiYanTopo {
	public static void main(String[] args) {
		/*if(args.length==0){
			System.out.println("param[1]:tipic name");
		}
		//countinfo
		String topicName = args[0];*/
		String topicName = "tvadmpv";
		String parseType = "parseUrl";
		TopoUtil topoUtil = new TopoUtil("bpd-huiyan-"+topicName+"-2a");
		final TopologyBuilder builder = new TopologyBuilder();
		/************* media data ***20180413************/
		builder.setSpout("bpd-huiyan-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig(topicName)), 8);
		builder.setBolt("LogParseBolt", new LogParseBolt(topicName,parseType), 10).setNumTasks(10).shuffleGrouping("bpd-huiyan-spout");
		builder.setBolt("LogDealBol", new LogDealBolt(topicName),10).shuffleGrouping("LogParseBolt");
		builder.setBolt("RedisBolt", new RedisBolt(), 10).shuffleGrouping("LogDealBol");
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-huiyan-topo",
					topoUtil.getConfig(20), builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
