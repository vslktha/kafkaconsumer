package com.amwater.rest.api.service;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import com.amwater.rest.api.common.KafkaUtility;

@RestControllerAdvice
public class DefaultService {
	private final static Logger log = Logger.getLogger(DefaultService.class);
	
	@ExceptionHandler(Exception.class)
	public String defaultMessage(HttpServletRequest request, Exception e) {
		String urlPath = (request.getQueryString() != null ? request.getRequestURI() + request.getQueryString() : request.getRequestURI());
		boolean success = false;
		String msg = e.getMessage();
		try {
			msg = InetAddress.getLocalHost().getHostName() + " is alive";
		} catch (UnknownHostException e1) {
			msg = (e1.getMessage() != null ? e1.getMessage() : e.getMessage());
			log.error("UnknownHostException occured during fetching hostname - ", e1);
		} catch (Exception e2) {
			msg = (e2.getMessage() != null ? e2.getMessage() : e.getMessage());
			log.error("Error encountered during default exception handling - ", e2);
		} finally {
			log.error("Default exception handler triggered for the request URL - '" + urlPath + "' with error - ", e);
		}
		return KafkaUtility.createResponse(success, msg);
	}
}
