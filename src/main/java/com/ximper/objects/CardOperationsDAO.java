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
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void recordReloadOperations(){
		
	}
	
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
	
	public void insertReloadTransactionLog(int denomId, int cashierId, int oldBalance, int newBalance, int topUpAmount, int bonusAmount) throws Exception{
		SimpleJdbcCall storeProc=new SimpleJdbcCall(jdbcTemplate).withProcedureName("INSERT_TOPUP_TRANSACTION_LOG");
		MapSqlParameterSource param=new MapSqlParameterSource();
		param.addValue("inDenomId", denomId);
		param.addValue("inOldBalance", oldBalance);
		param.addValue("inNewBalance", newBalance);
		param.addValue("inTopUpAmount", topUpAmount);
		param.addValue("inBonusAmount", bonusAmount);
		param.addValue("inCashierId", cashierId);
		SqlParameterSource params=param;
		storeProc.execute(params);
	}
}


