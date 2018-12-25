package com.radek.statki.atj;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.radek.statki.view.ShipController;

import javafx.application.Platform;

public class AsynchConsumerA implements MessageListener {
	private ShipController sc;

	public AsynchConsumerA(ShipController sc) {
		this.sc = sc;
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage)
			try {
				String information = ((TextMessage) message).getText();

				Platform.runLater(new Runnable() {
					public void run() {
						sc.onMessageReceived(information);
					}
				});

			} catch (JMSException e) {
				e.printStackTrace();
			}
	}
}
