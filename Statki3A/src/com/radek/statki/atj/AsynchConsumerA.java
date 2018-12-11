package com.radek.statki.atj;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.radek.statki.model.GameModel;

public class AsynchConsumerA implements MessageListener {
	private GameModel gm;
	public AsynchConsumerA(GameModel gm) {
		this.gm = gm;
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage)
			try {
				System.out.printf("Odebrano wiadomoœæ:'%s'%n", ((TextMessage) message).getText());
				String information = ((TextMessage) message).getText();
				gm.process(information);
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}
}
