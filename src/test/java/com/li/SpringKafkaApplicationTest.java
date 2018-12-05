package com.li;

/**
 * Created by wangdi on 17/7/2.
 */


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringKafkaApplicationTest {

   /* private static String HELLOWORLD_TOPIC = "helloworld.t";

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    *//*
       @ClassRule
       public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, HELLOWORLD_TOPIC);

      @BeforeClass
       public static void setUpBeforeClass() {
           System.setProperty("kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
       }
  *//* *//**//*
    @Before
    public void setUp() throws Exception {
        // wait until the partitions are assigned
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
                .getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    embeddedKafka.getPartitionsPerTopic());
        }
    }*//*

    @Test
    public void testReceive() throws Exception {
        sender.send(HELLOWORLD_TOPIC, " Kafka write write!");
        while (true){
            receiver.getLatch().await(1000, TimeUnit.MILLISECONDS);
            assertThat(receiver.getLatch().getCount()).isEqualTo(0);
        }

    }*/


}

