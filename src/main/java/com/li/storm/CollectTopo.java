package com.li.storm;


/**
 * test
 * */
public class CollectTopo {
	private  static String[] NIMBUS_HOST_REMOTE =  {"ckf159075.heracles.sohuno.com","ckf159076.heracles.sohuno.com"};
	private  static String[] ZK_HOST_REMOTE ={"10.11.159.82","10.11.159.83","10.11.159.84"};
	//BPD_tracking_PROD
	public static void main(String[] args) {
		/*SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts("kfm159082.heracles.sohuno.com:2181,kfm159083.heracles.sohuno.com:2181,kfm159084.heracles.sohuno.com:2181"),
				"BPD_tracking_XPS", "", "collect-spout-calculate-1");
		//collect-spout-calculate-1 测试
		kafkaConfig.startOffsetTime = OffsetRequest.LatestTime();
		Config config =new Config();
		config.put(Config.NIMBUS_SEEDS, Arrays.asList(NIMBUS_HOST_REMOTE)); //配置nimbus连接主机地址，比如：192.168.10.1
		config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST_REMOTE)); //配置zookeeper连接主机地址，可以使用集合存放多个
		//config.put(Config.STORM_ZOOKEEPER_PORT,2181); //配置zookeeper连接端口，默认2181*//*
		TopologyBuilder builder = new TopologyBuilder();
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		builder.setSpout("collect-spout", new KafkaSpout(kafkaConfig),10).setNumTasks(10);
		builder.setBolt("collect-bolt",new SplitBolt(),50).shuffleGrouping("collect-spout").setNumTasks(50);
		config.setNumWorkers(20);
		config.setDebug(false);
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			//System.out.println("--------"+System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("real-calculate",config , builder.createTopology());
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
