package com.ximper.stomp;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

@Component
public class StompHandler extends StompSessionHandlerAdapter{
	
	StompSession session;

	public StompSession getSession() {
		return session;
	}


	public void setSession(StompSession session) {
		this.session = session;
	}


	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		this.session=session;
		System.out.println("CONNECTED"+connectedHeaders+session);
    }
	

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		exception.printStackTrace();
	}

	@Override
	public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
		ex.printStackTrace();
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		Exception ex = new Exception(headers.toString());
		ex.printStackTrace();
		System.out.println(payload.toString());
	}
}
