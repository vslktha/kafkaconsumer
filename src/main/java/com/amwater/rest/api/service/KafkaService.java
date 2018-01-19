package com.amwater.rest.api.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amwater.rest.api.common.KafkaUtility;
import com.amwater.rest.api.model.KafkaModel;
import com.amwater.rest.api.model.KafkaRequest;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaService {

	private final static Logger log = Logger.getLogger(KafkaService.class);

	@Autowired
	private KafkaModel kafkaModel;

	@RequestMapping(value = "/message", method = RequestMethod.POST)
	public String addMessageToQueue(@RequestBody KafkaRequest request)
			throws FileNotFoundException, IOException, JSONException {

		boolean success = false;
		String msg = "Successfully processed the request";
		try {
			if (request != null && request.getTopic() != null && request.getMessage() != null) {
				String topic = request.getTopic();
				String message = request.getMessage();
				/**
				 * Validation of topic and message
				 */
				if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(message)) {
					msg = "Topic or message sent is empty";
				} else {
					success = kafkaModel.sendMessage(topic, message);
				}
			} else {
				msg = "Data missing to add to the queue";
			}
		} catch (Exception e) {
			log.error("Exception occured during serving the request for adding message to the queue - " + request.toString()
					+ " - Error is - ", e);
			msg = e.getMessage();
		}
		return KafkaUtility.createResponse(success, msg);
	}
	
	@RequestMapping(value = "/message/topic", method = RequestMethod.GET)
	public void readMessageFromTopic(@RequestParam("topic") String topicName, @RequestParam(value = "optional", required=false) String optional)
	{
		System.out.println("option="+optional);
		if(!topicName.isEmpty()){
			kafkaModel.recieveMessage(topicName, optional);
		}
	}
}




