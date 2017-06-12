package com.ximper.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.ximper.configurations.TransactionTypes;
import com.ximper.objects.CardOperationsDAO;
import com.ximper.objects.CardSalesObject;
import com.ximper.objects.InquiryObject;
import com.ximper.objects.PolicyObject;
import com.ximper.objects.ProductToAcquire;
import com.ximper.objects.TopUpResultObject;
import com.ximper.reader.CardConstants;
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

	private boolean isConnected(){
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
		}
		return true;
	}
	
//	private String getTagSerialNumber(String cardTag){
//		return Long.valueOf(cardTag, 16).toString();
//	}
	
	public void processReload(int denomId, int cashierId, String transactionTime){
		
		int newBalance=0;
		int newPoints=0;
		int newAdditionalOnNextReload=0;
		
		int oldBalance=0;
		int oldPoints=0;
		int oldAdditionalOnNextReload=0;
		int topUpAmount=0;
		int bonusAmount=0;
		
		try{
			if(isConnected()){
				String tagId=loyaltyCardReader.getTagId();
				if(loyaltyCardReader.isAuthenticatedBeforeAccess(CardConstants.CARD_KEY)){
					if(denomId==0){
						loyaltyCardReader.writeToCardMemory(CardConstants.LOAD_PAGE, 0);
						loyaltyCardReader.writeToCardMemory(CardConstants.POINTS_PAGE, 0);
						loyaltyCardReader.writeToCardMemory(CardConstants.ADDITIONAL_ON_NEXT_RELOAD_PAGE, 0);
						return;
					}
					
					oldBalance=loyaltyCardReader.readDataFromMemory(CardConstants.LOAD_PAGE);
					oldPoints=loyaltyCardReader.readDataFromMemory(CardConstants.POINTS_PAGE);
					oldAdditionalOnNextReload=loyaltyCardReader.readDataFromMemory(CardConstants.ADDITIONAL_ON_NEXT_RELOAD_PAGE);
					Map<String, Object> result=cardOperationsDAO.getTopUpValues(denomId, oldBalance, oldPoints, oldAdditionalOnNextReload);
					
					newBalance=(int)result.get("outnewbalance");
					newPoints=(int)result.get("outnewpoints");
					newAdditionalOnNextReload=(int)result.get("outnewadditionalonnextreload");
					topUpAmount=(int)result.get("outtopupamount");
					bonusAmount=(int)result.get("outbonusamount");
					
					if(loyaltyCardReader.writeToCardMemory(CardConstants.LOAD_PAGE, newBalance)){
						if(loyaltyCardReader.writeToCardMemory(CardConstants.POINTS_PAGE, newPoints)){
							if(loyaltyCardReader.writeToCardMemory(CardConstants.ADDITIONAL_ON_NEXT_RELOAD_PAGE, newAdditionalOnNextReload)){
								try{
									cardOperationsDAO.insertTransactionLog(denomId, cashierId, oldBalance, newBalance, topUpAmount, bonusAmount, tagId, transactionTime, 1);
									TopUpResultObject tObject=new TopUpResultObject();
									tObject.setBalanceAfterReload(newBalance);
									tObject.setBalanceBeforeReload(oldBalance);
									tObject.setLoadAmount(topUpAmount);
									tObject.setPointsAfterReload(newPoints);
									tObject.setPointsBeforeReload(oldPoints);
									sendResponse(tObject, TransactionTypes.RELOAD, ResponseStatus.OK, ResponseCodes.OK);
								}catch(Exception e){
									loyaltyCardReader.writeToCardMemory(CardConstants.LOAD_PAGE, oldBalance);
									loyaltyCardReader.writeToCardMemory(CardConstants.POINTS_PAGE, oldPoints);
									loyaltyCardReader.writeToCardMemory(CardConstants.ADDITIONAL_ON_NEXT_RELOAD_PAGE, oldAdditionalOnNextReload);
									sendResponse(null, TransactionTypes.RELOAD, ResponseStatus.ERROR, ResponseCodes.ERROR);
									e.printStackTrace();
								}
								
							}
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	public void processInquiry(){
		int oldBalance=0;
		int oldPoints=0;
		try{
			if(isConnected()){
				if(loyaltyCardReader.isAuthenticatedBeforeAccess(CardConstants.CARD_KEY))
				{
					oldBalance=loyaltyCardReader.readDataFromMemory(CardConstants.LOAD_PAGE);
					oldPoints=loyaltyCardReader.readDataFromMemory(CardConstants.POINTS_PAGE);
					InquiryObject iObject=new InquiryObject();
					iObject.setCardLoadBalance(oldBalance);
					iObject.setCardPointsBalance(oldPoints);
					sendResponse(iObject, TransactionTypes.INQUIRE, ResponseStatus.OK, ResponseCodes.OK);
				}
			}
			
		}catch(Exception e){
			try {
				sendResponse(null, TransactionTypes.INQUIRE, ResponseStatus.ERROR, ResponseCodes.ERROR);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		
	}
	
	public void processCardSales(int cardGroupId, int cashierId, String transactionTime){
		try{
			if(isConnected()){
				String tagId=loyaltyCardReader.getTagId();
				Map<String, Object> result=cardOperationsDAO.getCardValues(cardGroupId);
				int price=(int)result.get("outprice");
				int preloadedAmount=(int)result.get("outpreloadedamount");
				
				if(loyaltyCardReader.writeToCardMemory(CardConstants.LOAD_PAGE, preloadedAmount)){
					try{
						cardOperationsDAO.insertCardSaleTransactionLog(cashierId, cardGroupId, price, preloadedAmount, tagId,transactionTime);
						loyaltyCardReader.protectCardMemory();
						CardSalesObject cObject=new CardSalesObject();
						cObject.setCardGroupId(cardGroupId);
						cObject.setCardNumber(tagId);
						cObject.setCashierId(cashierId);
						sendResponse(cObject, TransactionTypes.SELL_CARD, ResponseStatus.OK, ResponseCodes.OK);
					}catch(Exception e){
						loyaltyCardReader.writeToCardMemory(CardConstants.LOAD_PAGE, 0);
						sendResponse(null, TransactionTypes.SELL_CARD, ResponseStatus.ERROR, ResponseCodes.ERROR);
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void sendResponse(Object responseObject, String transactionType, String status, String code) throws Exception{
		
		ApiResponse response=new ApiResponse();
		response.setApiData(responseObject);
		response.setTransactionType(transactionType);
		response.setStatus(status);
		response.setStatusCode(code);
		Gson gson=new Gson();
		sendMessage(gson.toJson(response));
		
	}
	
	public void processProductAcquire(List<ProductToAcquire> products, String transactionTime){
		int totalPrice=0;
		int remainingBalance=0;
		String insertCommand="";
		try{
			for(ProductToAcquire pAcquire:products){
				int productPrice=cardOperationsDAO.getPrice(pAcquire.getProductId());
				totalPrice=totalPrice+(pAcquire.getItemCount()*productPrice);
			}
			if(isConnected()){
				if(loyaltyCardReader.isAuthenticatedBeforeAccess(CardConstants.CARD_KEY)){
					int currentBalance=loyaltyCardReader.readDataFromMemory(CardConstants.LOAD_PAGE);
					try{
						if(currentBalance>=totalPrice && currentBalance>0){
							String tagId=loyaltyCardReader.getTagId();
							remainingBalance=currentBalance-totalPrice;
							if(loyaltyCardReader.writeToCardMemory(CardConstants.LOAD_PAGE, remainingBalance)){
								
							}
						}else{
							sendResponse(null, TransactionTypes.ACQUIRE_PRODUCT, ResponseStatus.ERROR, "INSUFFICIENT");
						}
	
						
					}catch(Exception e){
						
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
