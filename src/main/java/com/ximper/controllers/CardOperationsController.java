package com.ximper.controllers;

import javax.servlet.http.HttpServletResponse;

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


@RestController
public class CardOperationsController {

	@Autowired
	CardOperationsManager cardOperationsManager;
	
	@RequestMapping(value=TapEndpoints.TAP_CARD_BALANCE_TOPUP, method=RequestMethod.GET)
	public ApiResponse processReload(
			@PathVariable int productId,
			@PathVariable int businessId
			)
	{

		final int reloadProductid=productId;
		final int reloadBusinessId=businessId;
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.sendReload(reloadProductid, reloadBusinessId);
			}
		});
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	@RequestMapping(value=TapEndpoints.TAP_CARD_BALANCE_DEDUCT, method=RequestMethod.POST)
	public ApiResponse processDeduct(
			@RequestParam(required=false) double amount
			)
	{
	
		final double deductAmount=amount;
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.sendDeduct(deductAmount);;
			}
		});
		response.setApiData(null);
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
				cardOperationsManager.inquireBalance();
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

		final ClaimRewardRequest claimReq=claimRequest;
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.claimRewards(claimReq);
			}
		});
		response.setApiData(null);
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	@RequestMapping(value=TapEndpoints.ACQUIRE_PRODUCTS, method=RequestMethod.POST)
	public ApiResponse acquireProducts(
			@RequestBody AcquireProductRequest acquireRequest)
	{

		final AcquireProductRequest acquireReq=acquireRequest;
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.acquireProducts(acquireReq);
			}
		});
		response.setApiData(null);
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	@RequestMapping(value=TapEndpoints.TAP_CARD_SALES, method=RequestMethod.POST)
	public ApiResponse processCardSales(
			@RequestBody CardSalesRequest cardRequest)
	{

		final CardSalesRequest cardReq=cardRequest;
		ApiResponse response=new ApiResponse();
		ThreadPoolTaskExecutor tpExec=new ThreadPoolTaskExecutor();
		tpExec.initialize();
		TaskExecutor taskExecutor = tpExec;
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				cardOperationsManager.sellCard(cardReq);
			}
		});
		response.setApiData(null);
		response.setStatus(ResponseStatus.OK);
		response.setStatusCode(ResponseCodes.OK);
		return response;
	}
	
	
}