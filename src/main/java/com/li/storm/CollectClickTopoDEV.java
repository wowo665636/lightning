package com.li.storm;


@Deprecated
public class CollectClickTopoDEV {
	private  static String[] NIMBUS_HOST_REMOTE =  {"10.16.34.40"};
	private  static String[] ZK_HOST_REMOTE ={"10.10.84.21","10.10.84.14","10.10.84.22"};
	//BPD_tracking_PROD
	public static void main(String[] args) {
		/*SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts("10.10.84.221:2181,10.10.84.222:2181,10.10.84.223:2181"),
				"test", "", "bpd-collect-xpstrackingclick-test-1");///kafka-0.10.2.1-test
		kafkaConfig.startOffsetTime = OffsetRequest.LatestTime();
		Config config =new Config();
		config.put(Config.NIMBUS_SEEDS, Arrays.asList(NIMBUS_HOST_REMOTE)); //配置nimbus连接主机地址，比如：192.168.10.1
		config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST_REMOTE)); //配置zookeeper连接主机地址，可以使用集合存放多个
		//config.put(Config.STORM_ZOOKEEPER_PORT,2181); //配置zookeeper连接端口，默认2181*//*
		TopologyBuilder builder = new TopologyBuilder();
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		builder.setSpout("collect-spout", new KafkaSpout(kafkaConfig),90).setNumTasks(90);
		builder.setBolt("collect-bolt",new SplitClickBolt(),90).shuffleGrouping("collect-spout").setNumTasks(90);
		config.setMessageTimeoutSecs(60);
		config.setNumWorkers(45);
		config.setDebug(false);
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			//System.out.println("--------"+System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-real-xpstrackingclick-test",config , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}
*/

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
