package com.ximper.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.google.gson.Gson;
import com.ximper.configurations.ApiResponse;
import com.ximper.configurations.ApplicationValues;
import com.ximper.configurations.CardReaderMessages;
import com.ximper.configurations.ResponseCodes;
import com.ximper.configurations.ResponseStatus;
import com.ximper.objects.CardOperationsDAO;
import com.ximper.reader.LoyaltyCardReader;
import com.ximper.reader.ReaderStatusObject;
import com.ximper.stomp.StompHandler;

@Component
public class CardOperationsManager {

	@Autowired
	ApplicationValues applicationValues;
	
	@Autowired
	LoyaltyCardReader loyaltyCardReader;
	
	@Autowired
	CardOperationsDAO cardOperationsDAO;
	
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
				StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
				List<Transport> transports = new ArrayList<Transport>();
				transports.add(new WebSocketTransport(webSocketClient));
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

	public boolean connect(){
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
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}else if(readerMessage.equals(CardReaderMessages.NO_READER_ATTACHED)){
			ApiResponse response=new ApiResponse();
			response.setStatus(ResponseStatus.ERROR);
			response.setStatusCode(ResponseCodes.ERROR);
			response.setApiData(CardReaderMessages.NO_READER_ATTACHED);
			Gson gson=new Gson();
			try {
				sendMessage(gson.toJson(response));
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}else{
			if(readerStatus.getCardChannel()!=null){
				try {
					tagId=loyaltyCardReader.getUid(readerStatus.getCardChannel());
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	private String getTagSerialNumber(String cardTag){
		return Long.valueOf(cardTag, 16).toString();
	}
	
	public void processReload(int loadDenomId){
		int loadAmount=0;
		loyaltyCardReader.reload(loadAmount);
		cardOperationsDAO.processReload();
	}
	
}
