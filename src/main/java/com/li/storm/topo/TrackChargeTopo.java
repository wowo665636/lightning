package com.li.storm.topo;


import com.li.common.TopoUtil;
import com.li.storm.bolt.charge.TrackingChargeBolt;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;

/**
 * 计费topo-投放端流水数据推送
 */
public class TrackChargeTopo {
	public static void main(String[] args) {


		TopoUtil topoUtil = new TopoUtil("bpd-track-charge-1a");
		final TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("bpd-charge-spout", new KafkaSpout<>(topoUtil.bulidKafkaSpoutConfig("mbbi_bpd_tracking_charge")), 90);
		builder.setBolt("bpd-charge-bolt",new TrackingChargeBolt(),450).setNumTasks(450).shuffleGrouping("bpd-charge-spout");
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-track-charge",topoUtil.getConfig(90) , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}



		/*SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts(SPOUT_ZK_HOST,KAFKA_BROKER_ZK_PATH),
				"xpstrackingcharge", "/kafka-0.10.2.1/consumers", "bpd-track-charge-7p");///kafka-0.10.2.1-test
		//当服务器没有新消息时，消费者会等待这些时间
		kafkaConfig.fetchMaxWait= 10*60*1000;
		kafkaConfig.startOffsetTime = OffsetRequest.LatestTime();
		Config config =new Config();
		config.put(Config.NIMBUS_SEEDS, Arrays.asList(NIMBUS_HOST_REMOTE)); //配置nimbus连接主机地址，比如：192.168.10.1
		config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST_REMOTE)); //配置zookeeper连接主机地址，可以使用集合存放多个
		//config.put(Config.STORM_ZOOKEEPER_PORT,2181); //配置zookeeper连接端口，默认2181*//**//*
		TopologyBuilder builder = new TopologyBuilder();
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		builder.setSpout("bpd-charge-spout", new KafkaSpout(kafkaConfig),90);
		builder.setBolt("bpd-charge-bolt",new TrackingChargeBolt(),450).shuffleGrouping("bpd-charge-spout").setNumTasks(450);
		config.setMessageTimeoutSecs(60);
		config.setNumWorkers(90);
		config.setDebug(false);
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-track-charge",config , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}*/


		/******************************************/
		/*config.setDebug(true);
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("TOPOLOGY_ID_2",config,builder.createTopology());
		//
		Utils.sleep(50000);
		localCluster.killTopology("TOPOLOGY_ID_2");
		localCluster.shutdown();*/
	}

}
