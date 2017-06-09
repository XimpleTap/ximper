package com.ximper.objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class CardOperationsDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public void processReload(){
		
	}
}
