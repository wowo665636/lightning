package com.li.storm.sstics;


import com.li.redis.Constant;
import com.li.util.ConfigTopoStorm;


public class StatisticsClickTopo {
	public static void main(String[] args) {
		String[] NIMBUS_HOST_REMOTE = ((String) ConfigTopoStorm.getProMap().get(Constant.NIMBUS_HOST_REMOTE)).split(",");
		String[] ZK_HOST_REMOTE =((String) ConfigTopoStorm.getProMap().get(Constant.ZK_HOST_REMOTE)).split(",");
		String SPOUT_ZK_HOST = (String) ConfigTopoStorm.getProMap().get(Constant.SPOUT_ZK_HOST);
		String KAFKA_BROKER_ZK_PATH  = (String) ConfigTopoStorm.getProMap().get(Constant.KAFKA_BROKER_ZK_PATH);
/*
		SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts(SPOUT_ZK_HOST,KAFKA_BROKER_ZK_PATH),
				"xpstrackingclick", "/kafka-0.10.2.1/consumers", "bpd-statistics-c-1c");///kafka-0.10.2.1-test
		//当服务器没有新消息时，消费者会等待这些时间
		kafkaConfig.fetchMaxWait= 10*60*1000;
		kafkaConfig.startOffsetTime = OffsetRequest.LatestTime();
		Config config =new Config();
		config.put(Config.NIMBUS_SEEDS, Arrays.asList(NIMBUS_HOST_REMOTE)); //配置nimbus连接主机地址，比如：192.168.10.1
		config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST_REMOTE)); //配置zookeeper连接主机地址，可以使用集合存放多个
		//config.put(Config.STORM_ZOOKEEPER_PORT,2181); //配置zookeeper连接端口，默认2181*//*
		TopologyBuilder builder = new TopologyBuilder();
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		builder.setSpout("statistics-spout", new KafkaSpout(kafkaConfig),3).setNumTasks(3);
		builder.setBolt("statistics-bolt",new StatisticsClickBolt(),3).shuffleGrouping("statistics-spout").setNumTasks(3);
		config.setMessageTimeoutSecs(60);
		config.setNumWorkers(3);
		config.setDebug(false);
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-statistics-c",config , builder.createTopology());
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
