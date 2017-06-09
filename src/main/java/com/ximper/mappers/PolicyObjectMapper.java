package com.ximper.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ximper.objects.PolicyObject;

public class PolicyObjectMapper implements RowMapper<PolicyObject>{

	@Override
	public PolicyObject mapRow(ResultSet rs, int rowNum) throws SQLException {
		PolicyObject pObject=new PolicyObject();
		pObject.setAmountForLoad(rs.getInt("amount_for_load"));
		pObject.setCardRoaming(rs.getInt("is_card_roaming")==1?true:false);
		pObject.setCardValidity(rs.getInt("card_validity"));
		pObject.setDenomForPoints(rs.getInt("denom_for_points"));
		pObject.setIsReloadHaspoints(rs.getInt("is_reload_has_points")==1?true:false);
		pObject.setLoadForPoints(rs.getInt("load_for_points"));
		pObject.setLoadPriceInclusive(rs.getInt("is_load_price_inclusive")==1?true:false);
		pObject.setPerDenomPoints(rs.getInt("per_denom_points"));
		pObject.setPerTransactionPoints(rs.getInt("per_transaction_points"));
		pObject.setPointsConvertible(rs.getInt("is_point_convertible")==1?true:false);
		pObject.setPointsForLoad(rs.getInt("points_for_load"));
		pObject.setPointsInvalidityDay(rs.getInt("points_invalidity_day"));
		pObject.setPointsInvalidityMonth(rs.getInt("points_invalidity_month"));
		pObject.setPointsOnReload(rs.getInt("points_on_reload"));
		pObject.setRetainingPoint(rs.getInt("is_retaining_points")==1?true:false);
		pObject.setRewardDenomBased(rs.getInt("is_reward_denom_based")==1?true:false);
		pObject.setRewardTransactionBased(rs.getInt("is_reward_transaction_based")==1?true:false);
		return pObject;
	}

}
