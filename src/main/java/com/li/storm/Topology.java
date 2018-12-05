/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年7月27日 下午2:46:42
 * @Description: 无
 */
package com.li.storm;


import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;

import java.io.IOException;
@Deprecated
public class Topology {

	public static void main(String[] args) throws IOException, AlreadyAliveException, InvalidTopologyException, AuthorizationException {

		//Config conf = new Config();

		// 加载配置文件
	/*Config conf = new ConfigLoader().loadStormConfig(args.length > 0 ? args[0]
						: Default.STORM_CONF_PATH);*/
		/*System.out.println("----"+ JSON.toJSONString(ConfigUtil.getStrVal(conf, "kafka.zookeeper.servers")));*/
	/*	Config config = new ConfigLoader().loadStormConfig(args.length > 0 ? args[0]
				: Default.STORM_CONF_PATH);

		System.out.println(DateUtil.timestamp2Datetime2("1499326136"));

		SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts("kfm159082.heracles.sohuno.com:2181,kfm159083.heracles.sohuno.com:2181,kfm159084.heracles.sohuno.com:2181"),
				"BPD_tracking_PROD",
				"/consumer/BPD_tracking",
				UUID.randomUUID().toString());
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		// 构建 topo
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout-01-gn-kafka", new KafkaSpout(kafkaConfig),3);
		builder.setBolt("bolt1",new SplitBolt()).shuffleGrouping("spout-01-gn-kafka");*/




		//1 bolt-00-10
		/*builder.setBolt("bolt-00-10-msg2hashMap",new Msg2MapBolt(
						" MSISDN,DELAY,LAC,CI,IMEI,FLOW_TYPE,START_TIME,END_TIME,DURATION,UP_BYTES,DOWN_BYTES,TOTAL_BYTES,RAT_TYPE,TERMINAL_IP,VISIT_IP,STATUS,USER_AGENT,APN,IMSI,SGSNIP,GGSNIP,CONTENT_TYPE,SOURCE_PORT,DES_PORT,RECORD_MARK,MERGE_COUNT,HOST,URL"
						,"\\|"),
				Integer.parseInt("1"))
				.setNumTasks(Integer.parseInt("3"))
				.localOrShuffleGrouping("spout-01-gn-kafka");
		buildTopo(builder, conf);
		conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);*/
		// 提交topo
		//StormSubmitter.submitTopologyWithProgressBar(conf.get("myTest.storm").toString(), conf, builder.createTopology());
		/******************************************/
		/*LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("TOPOLOGY_ID_2",conf,builder.createTopology());
		//
		Utils.sleep(50000);
		localCluster.killTopology("TOPOLOGY_ID_2");
		localCluster.shutdown();*/


	}
	
	/*public static void buildTopo(TopologyBuilder builder,Config conf){

				KafkaSpout kafkaSpout = new KafkaSpoutFactory("BPD_tracking",
						"spout_id_01",
						//ConfigUtil.getStrArrayVal(conf, "kfm159082.heracles.sohuno.com:2181")
						new String[]{"kfm159082.heracles.sohuno.com","kfm159083.heracles.sohuno.com"},
						new StringScheme(),
						"/consumer/BPD_tracking",
						Integer.parseInt("2181"))
				        .getKafkaSpout();
				//0 spout-0-0
				builder.setSpout("spout-00-gn-kafka", kafkaSpout,1);
				//1 bolt-00-10
		builder.setBolt("bolt-00-10-msg2hashMap",new Msg2MapBolt(
				" MSISDN,DELAY,LAC,CI,IMEI,FLOW_TYPE,START_TIME,END_TIME,DURATION,UP_BYTES,DOWN_BYTES,TOTAL_BYTES,RAT_TYPE,TERMINAL_IP,VISIT_IP,STATUS,USER_AGENT,APN,IMSI,SGSNIP,GGSNIP,CONTENT_TYPE,SOURCE_PORT,DES_PORT,RECORD_MARK,MERGE_COUNT,HOST,URL"
				,"\\|"),
				Integer.parseInt("1"))
				.setNumTasks(Integer.parseInt("3"))
				.localOrShuffleGrouping("spout-00-gn-kafka");
		
	}*/

}




//kafka producer
//new KafkaProducer("test_topic").produce();		
		

//conf.setMessageTimeoutSecs(100);
//String[] zkServers= { "192.168.1.102", "192.168.103", "192.168.104" };
//String[] zkServers= ConfigUtil.getStringArrayValue(conf, "storm.zookeeper.servers.ip");
//conf.setNumWorkers(12);
// 输出统计指标值到日志文件中
//conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);