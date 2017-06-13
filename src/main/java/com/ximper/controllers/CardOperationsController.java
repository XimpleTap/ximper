package com.ximper.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ximper.configurations.ApiResponse;
import com.ximper.configurations.ResponseCodes;
import com.ximper.configurations.ResponseStatus;
import com.ximper.configurations.TapEndpoints;
import com.ximper.objects.AcquireProductRequest;
import com.ximper.objects.CardSalesRequest;
import com.ximper.objects.ClaimRewardRequest;
import com.ximper.objects.TopUpRequest;

@RestController
public class CardOperationsController {

	@Autowired
	CardOperationsManager cardOperationsManager;
	
	@RequestMapping(value=TapEndpoints.TAP_CARD_BALANCE_TOPUP, method=RequestMethod.POST)
	public ApiResponse processReload(
			@RequestBody TopUpRequest tObject
			)
	{
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.processReload(tObject.getDenomId(), tObject.getCashierId(), tObject.getTransactionTime());
			}
		});
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}

	@RequestMapping(value=TapEndpoints.TAP_CARD_BALANCE_INQUIRE, method=RequestMethod.GET)
	public ApiResponse inquireBalance()
	{
	
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.processInquiry();
			}
		});
		response.setApiData(null);
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	@RequestMapping(value=TapEndpoints.CLAIM_REWARDS, method=RequestMethod.POST)
	public ApiResponse claimRewards(@RequestBody ClaimRewardRequest claimRequest)
	{

		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				//cardOperationsManager.processRewardClaim(claimRequest.getRewardsToClaim(), claimRequest.getCashierId(), claimRequest.getTransactionTime());
			}
		});
		response.setApiData(null);
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	@RequestMapping(value=TapEndpoints.ACQUIRE_PRODUCTS, method=RequestMethod.POST)
	public ApiResponse acquireProducts(
			@RequestBody AcquireProductRequest aObject
			)
	{
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.processProductAcquire(aObject.getProductsToAcquire(), aObject.getCashierId(), aObject.getTransactionTime());
			}
		});
		response.setApiData(null);
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	@RequestMapping(value=TapEndpoints.TAP_CARD_SALES, method=RequestMethod.POST)
	public ApiResponse processCardSales(
			@RequestBody CardSalesRequest cObject
			)
	{
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.processCardSales(cObject.getGroupId(), cObject.getCashierId(), cObject.getTransactionTime());
			}
		});
		response.setApiData(null);
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	
}