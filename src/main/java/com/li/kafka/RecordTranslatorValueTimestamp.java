package com.li.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.kafka.spout.RecordTranslator;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.List;

/**
 * Created by wangdi on 18/3/22.
 */
public class RecordTranslatorValueTimestamp<K, V> implements RecordTranslator<K, V> {
    private static final long serialVersionUID = -2118500139470245815L;
    public  final Fields FIELDS = new Fields( "value", "timestamp");
    @Override
    public List<Object> apply(ConsumerRecord record) {
        return new Values(record.value(),record.timestamp());
    }
    @Override
    public Fields getFieldsFor(String stream) {
        return FIELDS;
    }

    @Override
    public List<String> streams() {
        return DEFAULT_STREAM;
    }
}
