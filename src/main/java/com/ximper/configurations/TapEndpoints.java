package com.ximper.configurations;

public class TapEndpoints {

	public static final String TAP_CARD_BALANCE_TOPUP					=	"/tap/card/{productId}/reload/{businessId}";
	public static final String TAP_CARD_BALANCE_DEDUCT					=	"/tap/card/{amount}/deduct";
	public static final String TAP_CARD_BALANCE_TOPUP_CANCEL			=	"/tap/card/reload/cancel";
	public static final String TAP_CARD_BALANCE_INQUIRE					=	"/tap/card/balance/inquire";
	public static final String CLAIM_REWARDS							=	"/tap/card/rewards/claim";
	public static final String ACQUIRE_PRODUCTS							=	"/tap/card/product/acquire";
	public static final String TAP_CARD_SALES							=	"/tap/card/sale";
	
}

