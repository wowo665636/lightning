package com.li.storm;


public class CollectTopoPROD {
	private  static String[] NIMBUS_HOST_REMOTE =  {"10.10.84.226"};
	private  static String[] ZK_HOST_REMOTE ={"testkf1.adrd.sohuno.com","testkf2.adrd.sohuno.com","testkf3.adrd.sohuno.com"};
	//BPD_tracking_PROD
	public static void main(String[] args) {
		/*SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts("testkf1.adrd.sohuno.com:2181,testkf2.adrd.sohuno.com:2181,testkf3.adrd.sohuno.com:2181"),
				"BPD_tracking_XPS", "", "bpd-collect-spout-calculate-prod-new-A");
		kafkaConfig.startOffsetTime = OffsetRequest.LatestTime();
		Config config =new Config();
		config.put(Config.NIMBUS_SEEDS, Arrays.asList(NIMBUS_HOST_REMOTE)); //配置nimbus连接主机地址，比如：192.168.10.1
		config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST_REMOTE)); //配置zookeeper连接主机地址，可以使用集合存放多个
		//config.put(Config.STORM_ZOOKEEPER_PORT,2181); //配置zookeeper连接端口，默认2181*//*
		TopologyBuilder builder = new TopologyBuilder();
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		builder.setSpout("collect-spout", new KafkaSpout(kafkaConfig),18).setNumTasks(18);
		builder.setBolt("collect-bolt",new SplitBolt(),36).shuffleGrouping("collect-spout").setNumTasks(36);
		config.setMessageTimeoutSecs(60);
		config.setNumWorkers(40);
		config.setDebug(false);
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			//System.out.println("--------"+System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-real-calculate-prod",config , builder.createTopology());
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
