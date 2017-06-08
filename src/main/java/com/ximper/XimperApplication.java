package com.ximper;

import org.eclipse.jetty.client.HttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;

import javax.smartcardio.*;

import com.ximper.configurations.CardReaderMessages;
import com.ximper.reader.LoyaltyCardReader;
import com.ximper.reader.ReaderStatusObject;

@SpringBootApplication(scanBasePackages="com.ximper")
@EnableAutoConfiguration(exclude = RepositoryRestMvcAutoConfiguration.class)
public class XimperApplication {

	public static void main(String[] args) {
		/*LoyaltyCardReader read=new LoyaltyCardReader();
	
		try {
			ReaderStatusObject readerStatus=read.connect();
			if(readerStatus.getConnectionMesssage().equals(CardReaderMessages.CONNECTION_OK)){
				String  tag=read.getTagId(readerStatus.getCardChannel());
				System.out.println(tag);
				try{
					
					System.out.println(a);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}else{
				
			}
			
		} catch (CardException e) {
			e.printStackTrace();
		} */
		
		SpringApplication.run(XimperApplication.class, args);
	}
	
	
}
