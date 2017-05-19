package com.ximper.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationValues {
	
	@Value("${rmq.stomp.host}")
	private String host;
	
	@Value("${rmq.stomp.queue}")
	private String queue;
	
	@Value("${rmq.stomp.login}")
	private String login;
	
	@Value("${rmq.stomp.passcode}")
	private String passcode;
	
	@Value("${rmq.stomp.port}")
	private int port;
	
	@Value("${api.server.baseurl}")
	private String apiUrl;
	
	@Value("${api.server.port}")
	private int apiPort;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPasscode() {
		return passcode;
	}
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public int getApiPort() {
		return apiPort;
	}
	public void setApiPort(int apiPort) {
		this.apiPort = apiPort;
	}
}
