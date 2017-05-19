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
	
	/*@Bean(name = "sqliteDataSource")
	@ConfigurationProperties(prefix="spring.db_sqlite") 
	public DataSource sqliteDataSource() {
	    return DataSourceBuilder.create().build();
	}
	 
	@Bean(name = "sqliteJdbcTemplate") 
    public JdbcTemplate sqliteJdbcTemplate(@Qualifier("sqliteDataSource") DataSource sqliteDb) { 
        return new JdbcTemplate(sqliteDb); 
    } */
	
	
}
