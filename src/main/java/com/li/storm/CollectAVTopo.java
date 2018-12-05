package com.li.storm;


import com.li.redis.Constant;
import com.li.util.ConfigTopoStorm;

@Deprecated
public class CollectAVTopo {

	//BPD_tracking_PROD
	public static void main(String[] args) {
		String[] NIMBUS_HOST_REMOTE = ((String) ConfigTopoStorm.getProMap().get(Constant.NIMBUS_HOST_REMOTE)).split(",");
		String[] ZK_HOST_REMOTE =((String) ConfigTopoStorm.getProMap().get(Constant.ZK_HOST_REMOTE)).split(",");
		String SPOUT_ZK_HOST = (String) ConfigTopoStorm.getProMap().get(Constant.SPOUT_ZK_HOST);
		String KAFKA_BROKER_ZK_PATH  = (String) ConfigTopoStorm.getProMap().get(Constant.KAFKA_BROKER_ZK_PATH);
		/*SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts(SPOUT_ZK_HOST,KAFKA_BROKER_ZK_PATH),
				"xpstrackingav", "/kafka-0.10.2.1/consumers", "bpd-xpstrackingav-3c");

		*//*SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts("zk1.adrd.sohuno.com:2181,zk2.adrd.sohuno.com:2181,zk3.adrd.sohuno.com:2181,zk4.adrd.sohuno.com:2181,zk5.adrd.sohuno.com:2181/kafka-0.10.2.1"),
				"xpstrackingav", "/kafka-0.10.2.1/consumers", "bpd-collect-xpstrackingav-f");*//*
		kafkaConfig.fetchMaxWait= 10*60*1000;//当服务器没有新消息时，消费者会等待这些时间
		kafkaConfig.startOffsetTime = OffsetRequest.LatestTime();
		System.out.println("NIMBUS_HOST_REMOTE:"+ JSON.toJSONString(NIMBUS_HOST_REMOTE));
		System.out.println("ZK_HOST_REMOTE:"+ JSON.toJSONString(ZK_HOST_REMOTE));
		System.out.println("SPOUT_ZK_HOST:"+SPOUT_ZK_HOST);
		System.out.println("KAFKA_BROKER_ZK_PATH:"+KAFKA_BROKER_ZK_PATH);
		Config config =new Config();
		config.put(Config.NIMBUS_SEEDS, Arrays.asList(NIMBUS_HOST_REMOTE)); //配置nimbus连接主机地址，比如：192.168.10.1
		config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST_REMOTE)); //配置zookeeper连接主机地址，可以使用集合存放多个
		//Map<String,String> map=new HashMap<>();
		//map.put("metadata.broker.list", "localhost:9092");
		//map.put("bootstrap.servers", "kf01.adrd.sohuno.com:8092,kf02.adrd.sohuno.com:8092,kf03.adrd.sohuno.com:8092,kf04.adrd.sohuno.com:8092,kf05.adrd.sohuno.com:8092,kf06.adrd.sohuno.com:8092,kf07.adrd.sohuno.com:8092,kf08.adrd.sohuno.com:8092,kf09.adrd.sohuno.com:8092,kf10.adrd.sohuno.com:8092,kf11.adrd.sohuno.com:8092,kf12.adrd.sohuno.com:8092");
		//map.put("serializer.class","kafka.serializer.StringEncoder");
		//config.put("kafka.broker.properties",map);
		TopologyBuilder builder = new TopologyBuilder();
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		builder.setSpout("collect-spout", new KafkaSpout(kafkaConfig),36).setNumTasks(36);
		builder.setBolt("collect-bolt",new SplitAVBolt(),36).shuffleGrouping("collect-spout").setNumTasks(36);
		config.setMessageTimeoutSecs(60);
		config.setNumWorkers(50);
		config.setDebug(false);
		try {
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			//System.out.println("--------"+System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-real-xpstrackingav",config , builder.createTopology());
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
