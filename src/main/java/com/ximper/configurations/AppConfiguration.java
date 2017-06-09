package com.ximper.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ximper.controllers.CardOperationsManager;
import com.ximper.reader.LoyaltyCardReader;

@Configuration
public class AppConfiguration {

	@Bean
	public LoyaltyCardReader getLoyaltyTapReader(){
		return new LoyaltyCardReader();
	}

	@Bean
	public ApplicationValues getApplicationValues(){
		return new ApplicationValues();
	}
	
	@Bean
	public CardOperationsManager getCardOperationsManager(){
		return new CardOperationsManager();
	}
	
	
	
}
