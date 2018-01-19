package com.amwater.rest.api.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amwater.rest.api.common.KafkaUtility;

@Component
public class KafkaModel {

	private final static Logger log = Logger.getLogger(KafkaModel.class);

	@Autowired
	private KafkaUtility kafkaUtility;
	private Properties properties;

	public boolean sendMessage(String topic, String message) throws Exception {
		Producer<String, String> producer = null;
		boolean success = true;
		try {
			if (properties == null) {
				/**
				 * Load default properties
				 */
				properties = kafkaUtility.loadProperties();
			}
			/**
			 * Add message to the kafka queue
			 */
			producer = new KafkaProducer<String, String>(properties);
			producer.send(new ProducerRecord<String, String>(topic, message));
		} catch (Exception e) {
			success = false;
			log.error("Exception occured during adding message to the queue - Topic is '" + topic + "' and message is '" + message + "' - Error is - ", e);
			throw e;
		} finally {
			if (producer != null) {
				producer.close();
			}
		}
		return success;
	}
	
	public boolean recieveMessage(String topic, String optional) {
		boolean success = true;
		boolean seek = true;
		Properties props = new Properties();
		KafkaConsumer<String, String> consumer = null;
		try {
		if (properties == null) {
			properties = kafkaUtility.loadProperties(); 
			props.put("bootstrap.servers", "localhost:9092");
		      props.put("group.id", "test");
		      props.put("enable.auto.commit", "true");
		      props.put("auto.commit.interval.ms", "1000");
		      props.put("session.timeout.ms", "30000");
		      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		}
		consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(topic));

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			
			if(optional != null){
			Set<TopicPartition> partitions = records.partitions();
			for(TopicPartition part : partitions){
			consumer.seek(part, 0);
			seek = false;
			}
			}
			
	         for (ConsumerRecord<String, String> record : records){
	         System.out.printf("offset = %d, key = %s, value = %s\n", 
	            record.offset(), record.key(), record.value());
	         }
	         if(!seek){
	        	 break;
	         }
	         
		}
		}
		catch (Exception e) {
			success = false;
			log.error("Exception occured during recieving message from the queue - Topic is '" + topic + "'and - Error is - ", e);
		} 
		finally {
			if (consumer != null) {
				consumer.close();
			}
		}
		return success;
	}
}