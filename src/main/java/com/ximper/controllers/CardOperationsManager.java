package com.ximper.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;

import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.JettyXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.google.gson.Gson;
import com.ximper.configurations.AcquireProductApiResponse;
import com.ximper.configurations.ApiResponse;
import com.ximper.configurations.ApplicationValues;
import com.ximper.configurations.CardReaderMessages;
import com.ximper.configurations.ClaimRewardApiResponse;
import com.ximper.configurations.InquiryApiResponse;
import com.ximper.configurations.ResponseCodes;
import com.ximper.configurations.ResponseStatus;
import com.ximper.configurations.TapApiServerEndpoints;
import com.ximper.configurations.TopupApiResponse;
import com.ximper.objects.AcquireProductRequest;
import com.ximper.objects.ApiAcquireProductRequest;
import com.ximper.objects.ApiCardSalesRequest;
import com.ximper.objects.ApiClaimRewardRequest;
import com.ximper.objects.CardSalesRequest;
import com.ximper.objects.ClaimRewardRequest;
import com.ximper.objects.ClaimRewardResult;
import com.ximper.objects.DeductObject;
import com.ximper.objects.ReloadObject;
import com.ximper.objects.TopUpResultObject;
import com.ximper.reader.LoyaltyCardReader;
import com.ximper.reader.ReaderStatusObject;
import com.ximper.stomp.StompHandler;

@Component
public class CardOperationsManager {

	@Autowired
	ApplicationValues applicationValues;
	
	@Autowired
	LoyaltyCardReader loyaltyCardReader;
	
	StompSession session;
	StompHandler stompProducer;
	
	private void connectToStompServer() throws Exception{
			try{
				StompHeaders stompHeaders=new StompHeaders();
				stompHeaders.add("login", applicationValues.getLogin());
				stompHeaders.add("passcode",  applicationValues.getPasscode());
				WebSocketHttpHeaders socketHeaders=new WebSocketHttpHeaders();
				socketHeaders.add("login", applicationValues.getLogin());
				socketHeaders.add("passcode",  applicationValues.getPasscode());
				//HttpClient jettyHttpClient = new HttpClient();
				//jettyHttpClient.start();
				
				StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
		
				List<Transport> transports = new ArrayList<Transport>();
				transports.add(new WebSocketTransport(webSocketClient));
				//transports.add(new JettyXhrTransport(jettyHttpClient));
				SockJsClient sockJsClient = new SockJsClient(transports);

				 
				String stompUrl = "ws://"+applicationValues.getHost()+":"+applicationValues.getPort()+"/stomp";
				StompHandler stompProducer = new StompHandler();			
				WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
	
				
				stompClient.setMessageConverter(new StringMessageConverter());
				stompClient.setDefaultHeartbeat(new long[] {0, 0});
				stompClient.connect(stompUrl, socketHeaders, stompHeaders, stompProducer);
				Thread.sleep(5000);
				System.out.println("Waiting for session to be established");
				session=stompProducer.getSession();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	
	public void sendMessage(String message) throws Exception{
		if(session==null){
			System.out.println("Session is null:Starting connection");
			connectToStompServer();
		}else{
			session.send(applicationValues.getQueue(), message);
			//log here
			System.out.println("STOMP message sent");
			return;
		}
		if(session!=null){
			session.send(applicationValues.getQueue(), message);
			//log here
			System.out.println("STOMP message sent");
		}
	}
	
	@SuppressWarnings("restriction")
	public String getTagId(){
		String tagId="";
		ReaderStatusObject readerStatus=loyaltyCardReader.connect();
		String readerMessage=readerStatus.getConnectionMesssage();
		if(readerMessage.equals(CardReaderMessages.CARD_NOT_PRESENT)){
			ApiResponse response=new ApiResponse();
			response.setStatus(ResponseStatus.ERROR);
			response.setStatusCode(ResponseCodes.ERROR);
			response.setApiData(CardReaderMessages.CARD_NOT_PRESENT);
			Gson gson=new Gson();
			try {
				sendMessage(gson.toJson(response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(readerMessage.equals(CardReaderMessages.NO_READER_ATTACHED)){
			ApiResponse response=new ApiResponse();
			response.setStatus(ResponseStatus.ERROR);
			response.setStatusCode(ResponseCodes.ERROR);
			response.setApiData(CardReaderMessages.NO_READER_ATTACHED);
			Gson gson=new Gson();
			try {
				sendMessage(gson.toJson(response));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			if(readerStatus.getCardChannel()!=null){
				try {
					tagId=loyaltyCardReader.getTagId(readerStatus.getCardChannel());
				} catch (CardException e) {
					e.printStackTrace();
				}
			}
		}
		return tagId;
	}
	

	public void sendReload(int reloadProductId, int businessId){
		try {
			String tagId=getTagId();
			if(tagId!=""){
				ReloadObject reload=new ReloadObject();
				reload.setCardNumber(getTagSerialNumber(tagId));
				reload.setProductId(reloadProductId);
				reload.setBusinessId(businessId);
				RestTemplate restClient=new RestTemplate();
				TopupApiResponse response=restClient.postForObject(applicationValues.getApiUrl()+TapApiServerEndpoints.CARD_RELOAD, reload, TopupApiResponse.class);
				sendJsonMessage(response);
			}
		} catch (Exception ex) {
			sendApiConnectionStatus(ex.getMessage());
			ex.printStackTrace();
			
		}
		
	}

	public void sendDeduct(double deductAmount){
		try {
			String tagId=getTagId();
			if(tagId!=""){
				DeductObject deduct=new DeductObject();
				deduct.setCardNumber(tagId);
				deduct.setTopUpAmount(deductAmount);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void claimRewards(ClaimRewardRequest claimRequest){
		try {
			String tagId=getTagId();
			if(tagId!=""){
				RestTemplate restClient=new RestTemplate();
				String url=(applicationValues.getApiUrl()+TapApiServerEndpoints.REWARDS_CLAIM);
				ApiClaimRewardRequest claimReq=new ApiClaimRewardRequest();
				claimReq.setBusinessId(claimRequest.getBusinessId());
				claimReq.setCardNumber(getTagSerialNumber(tagId));
				claimReq.setRewardsToClaim(claimRequest.getRewardsToClaim());
				claimReq.setCashierId(claimRequest.getCashierId());
				ClaimRewardApiResponse response=restClient.postForObject(url, claimReq, ClaimRewardApiResponse.class);
				sendJsonMessage(response);
			}
		}catch (Exception ex) {
			sendApiConnectionStatus(ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	@SuppressWarnings({"restriction"})
	public void inquireBalance(){
		try {
			String tagId=getTagId();
			if(tagId!=""){
				String cardNumber=getTagSerialNumber(tagId);
				RestTemplate restClient=new RestTemplate();
				String url=(applicationValues.getApiUrl()+TapApiServerEndpoints.CARD_INQUIRE_BALANCE).replace("{cardNumber}", cardNumber);
				InquiryApiResponse response=restClient.getForObject(url, InquiryApiResponse.class);
				sendJsonMessage(response);
			}
		} catch (CardException e) {
			sendApiConnectionStatus(e.getMessage());
			e.printStackTrace();
		}catch (Exception ex) {
			sendApiConnectionStatus(ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public void acquireProducts(AcquireProductRequest acquireRequest){
		try {
			String tagId=getTagId();
			if(tagId!=""){
				ApiAcquireProductRequest apiRequest=new ApiAcquireProductRequest();
				apiRequest.setCardNumber(getTagSerialNumber(tagId));
				apiRequest.setBusinessId(acquireRequest.getBusinessId());
				apiRequest.setProductsToAcquire(acquireRequest.getProductsToAcquire());
				RestTemplate restClient=new RestTemplate();
				String url=applicationValues.getApiUrl()+TapApiServerEndpoints.PRODUCTS_SERVICES_ACQUIRE;
				AcquireProductApiResponse response=restClient.postForObject(url, apiRequest, AcquireProductApiResponse.class);
				sendJsonMessage(response);
			}
		} catch (Exception ex) {
			sendApiConnectionStatus(ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public void sellCard(CardSalesRequest cardRequest){
		try {
			String tagId=getTagId();
			if(tagId!=""){
				ApiCardSalesRequest cardReq=new ApiCardSalesRequest();
				cardReq.setCardNumber(getTagSerialNumber(tagId));
				cardReq.setBusinessId(cardRequest.getBusinessId());
				cardReq.setCardGroupId(cardRequest.getCardGroupId());
				cardReq.setCashierId(cardRequest.getCardGroupId());
				RestTemplate restClient=new RestTemplate();
				String url=applicationValues.getApiUrl()+TapApiServerEndpoints.CARD_SELL;
				ApiResponse response=restClient.postForObject(url, cardReq, ApiResponse.class);
				sendJsonMessage(response);
			}
		} catch (Exception ex) {
			sendApiConnectionStatus(ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	private String getTagSerialNumber(String cardTag){
		return Long.valueOf(cardTag, 16).toString();
	}
	
	private Object sendRequest(RequestMethod requestMethod, Object requestObject, String requestPath, Object response){
		RestTemplate requestSender=new RestTemplate();
		if(requestMethod.equals(RequestMethod.POST)){
			requestSender.postForObject(applicationValues.getApiUrl()+requestPath, requestObject, response.getClass());
		}else if(requestMethod.equals(RequestMethod.GET)){
			requestSender.getForObject(applicationValues.getApiUrl()+requestPath, response.getClass(), new Object[]{});
		}
		return response;
	}
	
	private void sendJsonMessage(Object object) throws Exception{
		Gson gson=new Gson();
		String jsonMessage=gson.toJson(object);
		sendMessage(jsonMessage);
	}
	
	private void sendApiConnectionStatus(String message){
		ApiResponse response=new ApiResponse();
		response.setStatus(ResponseStatus.ERROR);
		response.setStatusCode(ResponseCodes.ERROR);
		response.setApiData(message);
		try {
			sendJsonMessage(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
