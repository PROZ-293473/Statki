package com.radek.statki.atj;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

import com.radek.statki.model.GameModel;

public class ConsumerA {
	private JMSContext jmsContext;
	private JMSConsumer jmsConsumer;
	private Queue queue;
	private GameModel gm;

	public ConsumerA(String url, String queueName, GameModel gm) throws JMSException {
		this.gm = gm;
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		jmsContext = connectionFactory.createContext();

		// 7676 numer portu, na ktorym JMS Service nasluchuje polaczen
		// [hostName][:portNumber][/serviceName] np. "localhost:7676/jms"

		((com.sun.messaging.ConnectionFactory) connectionFactory)
				.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, url);
		queue = new com.sun.messaging.Queue(queueName); // "ATJQueue"
		jmsConsumer = jmsContext.createConsumer(queue);
	}

	public String receiveQueueMessage() throws JMSException {
		Message msg = jmsConsumer.receive(10); // 10 ms
		if (msg instanceof TextMessage)
			return ((TextMessage) msg).getText();
		return null;
	}

	public void receiveQueueMessageAsync() throws InterruptedException {
		jmsConsumer.setMessageListener(new AsynchConsumerA(gm));
		for (int i = 0; i < 30; ++i) { // 30 sekund
			System.out.println("Konsument A wykonuje zadanie");
			Thread.sleep(100);
		}
	}

	public void finalize() {
		if (jmsConsumer != null)
			jmsConsumer.close();
		if (jmsContext != null)
			jmsContext.close();
	}
}
