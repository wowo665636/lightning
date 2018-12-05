/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年7月24日 下午2:52:38
 * @Modified: 2015年10月20日 gsw
 * @Description: 无
 */
package com.li.kafka;

/**
 * 
 * kafka spout factory 
 *
 */
@Deprecated
public class KafkaSpoutFactory {
/*

	private BrokerHosts brokerHosts;
	private SpoutConfig spoutConf;
	private KafkaSpout kafkaSpout;
	// 默认参数
	private static final String ZKSERVERS = "kafka.zookeeper.servers"; // 记录Spout读取进度所用的zookeeper的host
	private static final String ZKROOT = "kafka.topic.zookeeper.root";// 进度信息记录于zookeeper的哪个路径下
	private static final Scheme SCHEME = new StringScheme();
	private static Logger logger = LoggerFactory.getLogger(KafkaSpoutFactory.class);
	private static Config config;



	static {
		try {
			config = new ConfigLoader().loadStormConfig(Default.STORM_CONF_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public KafkaSpoutFactory (String topic, String topologyId) {
		this(topic, topologyId, (String) config.get(ZKSERVERS), new StringScheme(), (String) config.get(ZKROOT));
	}

	*/
/**
	 * Constructor
	 *
	 * @param topic
	 * @param topologyId
	 * @param brokerZkStr
	 * @param scheme
	 * @param zkRoot
	 *//*

	public KafkaSpoutFactory(String topic, String topologyId, String brokerZkStr,
							 Scheme scheme, String zkRoot) {
		this.brokerHosts = new ZkHosts(brokerZkStr);
		this.spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, topologyId);
		this.spoutConf.scheme = new SchemeAsMultiScheme(scheme);
		//this.spoutConf.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
		this.kafkaSpout = new KafkaSpout(this.spoutConf);

	}
	public KafkaSpout getKafkaSpout() {
		return kafkaSpout;
	}

	public Config getConfig(){
		return config;
	}
*/





}

