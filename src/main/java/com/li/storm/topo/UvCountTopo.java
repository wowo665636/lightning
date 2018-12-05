package com.li.storm.topo;


import com.li.storm.bolt.sstics.UvCountBolt;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;

import java.util.Arrays;
import java.util.Properties;


public class UvCountTopo {
	public static void main(String[] args) {
		//  /stormYZ/kafka-0.10.2.1/consumers
		//bpd-track-customization-13b


		final TopologyBuilder builder = new TopologyBuilder();
		/************* media data ***************/
		builder.setSpout("bpd-uvCount-spout", new KafkaSpout<>(bulidKafkaSpoutConfig("xpstrackingcharge")), 90);
		builder.setBolt("bpd-uvCount-bolt",new UvCountBolt(),180).setNumTasks(360).shuffleGrouping("bpd-uvCount-spout");

		try {
			Config config = getConfig();
			//config.put(config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);
			System.setProperty("storm.jar", System.getProperty("user.dir") + "/target/lightning-jar-with-dependencies.jar");
			StormSubmitter.submitTopologyWithProgressBar("bpd-track-uvCount",config , builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}


	/*config.setDebug(true);
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("TOPOLOGY_ID_2",config,builder.createTopology());
		//
		Utils.sleep(50000);
		localCluster.killTopology("TOPOLOGY_ID_2");
		localCluster.shutdown();*/


		/******************************************/

	}

	/**
	 * 构建 kafkaSpoutConfig
	 * @param topics
	 * @return
	 */
	public static KafkaSpoutConfig bulidKafkaSpoutConfig(String ... topics){
		return KafkaSpoutConfig.builder("kf01.adrd.sohuno.com:8092,kf02.adrd.sohuno.com:8092,kf03.adrd.sohuno.com:8092,kf04.adrd.sohuno.com:8092,kf05.adrd.sohuno.com:8092,kf06.adrd.sohuno.com:8092,kf07.adrd.sohuno.com:8092,kf08.adrd.sohuno.com:8092,kf09.adrd.sohuno.com:8092,kf10.adrd.sohuno.com:8092,kf11.adrd.sohuno.com:8092,kf12.adrd.sohuno.com:8092,kf13.adrd.sohuno.com:8092,kf14.adrd.sohuno.com:8092,kf15.adrd.sohuno.com:8092,kf16.adrd.sohuno.com:8092", topics)
				.setOffsetCommitPeriodMs(1000)
				.setProp(getConsumerProperties())
				.setMaxUncommittedOffsets(1000)
				.setFirstPollOffsetStrategy(KafkaSpoutConfig.FirstPollOffsetStrategy.UNCOMMITTED_LATEST)
				//.setRecordTranslator(new RecordTranslatorValueTimestamp<>() )
				.build();
	}
	/**
	 * 获取消费 mp kafka 集群配置
	 * @return
	 */
	private static Properties getConsumerProperties(){
		Properties props = new Properties();
		props.setProperty("group.id", "bpd-track-uvCount-5a");
		props.setProperty("enable.auto.commit", "true");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("acks", "1");
		return props;
	}
	/**
	 * 获取storm配置
	 * @return
	 */
	private static Config getConfig() {
		Config config = new Config();
		config.put(Config.NIMBUS_SEEDS, Arrays.asList("styz15121.heracles.sohuno.com"));
		//config.setMaxSpoutPending(100);
		config.setMessageTimeoutSecs(60);
		config.setNumWorkers(300);
		config.setDebug(false);
		config.setMessageTimeoutSecs(10000000);
		return config;
	}



}
