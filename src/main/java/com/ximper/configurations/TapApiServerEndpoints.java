package com.ximper.configurations;

public class TapApiServerEndpoints {
	
	
	//CardsController
	public static final String CARD_RELOAD					=	"/cards/reload";
	public static final String CANCEL_CARD_RELOAD			=	"/cards/reload/cancel";
	public static final String CARD_INQUIRE_BALANCE			=	"/cards/balance/{cardNumber}/inquire";
	public static final String CARD_SELL					=	"/cards/sell";
	
	//ProductsServiceController
	public static final String PRODUCTS_SERVICES			=	"/products/{businessId}";
	public static final String PRODUCTS_SERVICES_ACQUIRE	=	"/products/acquire";
	
	//RewardsController
	public static final String REWARDS_AVAILABLE			=	"/rewards/{businessId}";
	public static final String REWARDS_CLAIM				=	"/rewards/claim/";
	
}

