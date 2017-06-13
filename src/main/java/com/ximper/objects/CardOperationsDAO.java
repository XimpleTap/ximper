package com.ximper.objects;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.ximper.mappers.PolicyObjectMapper;

@Component
public class CardOperationsDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public PolicyObject getPolicy() throws Exception{
		return jdbcTemplate.queryForObject("SELECT * FROM policies LIMIT 1", new PolicyObjectMapper(), new Object[]{});
	}

	public Map<String,Object> getTopUpValues(int denomId, int oldBalance, int oldPoints, int oldAdditionalOnNextReload) throws Exception{
		SimpleJdbcCall storeProc=new SimpleJdbcCall(jdbcTemplate).withProcedureName("TOPUP_CARD_OFFLINE");
		MapSqlParameterSource param=new MapSqlParameterSource();
		param.addValue("inDenomId", denomId);
		param.addValue("inOldBalance", oldBalance);
		param.addValue("inOldPoints", oldPoints);
		param.addValue("inOldAdditionalOnNextReload", oldAdditionalOnNextReload);
		SqlParameterSource params=param;
		Map<String, Object> result=storeProc.execute(params);
		return result;
	}
	
	public Map<String,Object> getCardValues(int cardGroupId) throws Exception{
		SimpleJdbcCall storeProc=new SimpleJdbcCall(jdbcTemplate).withProcedureName("GET_CARD_GROUP_DETAIL");
		MapSqlParameterSource param=new MapSqlParameterSource();
		param.addValue("inCardGroupId", cardGroupId);
		SqlParameterSource params=param;
		Map<String, Object> result=storeProc.execute(params);
		return result;
	}
	
	public Map<String, Object> getPriceAndName(int productId) throws Exception{
		SimpleJdbcCall storeProc=new SimpleJdbcCall(jdbcTemplate).withProcedureName("GET_PRICE_AND_NAME");
		MapSqlParameterSource param=new MapSqlParameterSource();
		param.addValue("inProductId", productId);
		SqlParameterSource params=param;
		Map<String, Object> result=storeProc.execute(params);
		return result;
	}
	
	public void insertTransactionLog(int denomId, int cashierId, int oldBalance, int newBalance, int topUpAmount, int bonusAmount, String tagId, String transactionTime, int transactionType, int itemCount) throws Exception{
		SimpleJdbcCall storeProc=new SimpleJdbcCall(jdbcTemplate).withProcedureName("INSERT_TRANSACTION_LOG");
		MapSqlParameterSource param=new MapSqlParameterSource();
		param.addValue("inDenomId", denomId);
		param.addValue("inOldBalance", oldBalance);
		param.addValue("inNewBalance", newBalance);
		param.addValue("inTopUpAmount", topUpAmount);
		param.addValue("inBonusAmount", bonusAmount);
		param.addValue("inCashierId", cashierId);
		param.addValue("inTagId", tagId);
		param.addValue("inTransactionTime", transactionTime);
		param.addValue("inTransactionType", transactionType);
		param.addValue("inItemCount", itemCount);
		SqlParameterSource params=param;
		storeProc.execute(params);
	}
	
	public void insertCardSaleTransactionLog(int cashierId, int cardGroupId, int cardPrice, int preloadedAmount, String tagId, String transactionTime) throws Exception{
		SimpleJdbcCall storeProc=new SimpleJdbcCall(jdbcTemplate).withProcedureName("INSERT_CARD_SALES_TRANSACTION_LOG");
		MapSqlParameterSource param=new MapSqlParameterSource();
		param.addValue("inCardGroupId", cardGroupId);
		param.addValue("inCashierId", cashierId);
		param.addValue("inPrice", cardPrice);
		param.addValue("inPreloadedAmount", preloadedAmount);
		param.addValue("inTagId", tagId);
		param.addValue("inTransactionTime", transactionTime);
		SqlParameterSource params=param;
		storeProc.execute(params);
	}
}


