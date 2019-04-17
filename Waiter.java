//  Parker Combs
//  parker.combs@und.edu
//  CSCI 364
//  Assignment 4
package waiter;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Waiter implements MessageListener {
	private Connection connection;
	private Session session;
	private Destination aQueue;
	private Destination bQueue;
	private Destination cQueue;
	private Destination dQueue;
	private Destination eQueue;
	private Destination wQueue;
	private MessageProducer aProducer;
	private MessageProducer bProducer;
	private MessageProducer cProducer;
	private MessageProducer dProducer;
	private MessageProducer eProducer;
	private MessageConsumer wConsumer;

	private boolean f0;
	private boolean f1;
	private boolean f2;
	private boolean f3;
	private boolean f4;

	public Waiter(Connection conn) {
		connection = conn;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			aQueue = session.createQueue("hw4.queue.Alpha");
			bQueue = session.createQueue("hw4.queue.Bravo");
			cQueue = session.createQueue("hw4.queue.Charlie");
			dQueue = session.createQueue("hw4.queue.Delta");
			eQueue = session.createQueue("hw4.queue.Echo");
			wQueue = session.createQueue("hw4.queue.Waiter");
			aProducer = session.createProducer(aQueue);
			bProducer = session.createProducer(bQueue);
			cProducer = session.createProducer(cQueue);
			dProducer = session.createProducer(dQueue);
			eProducer = session.createProducer(eQueue);
			wConsumer = session.createConsumer(wQueue);
			wConsumer.setMessageListener(this);
		} catch (JMSException jmse) {
			System.err.println("Error initializing waiter.");
			System.exit(1);
		}

		f0 = false;
		f1 = false;
		f2 = false;
		f3 = false;
		f4 = false;
	}

	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage tm = (TextMessage) message;
				String msg = tm.getText();
				String[] msgSplit = msg.split(" ");
				String id = msgSplit[0]; // philosopher
				String action = msgSplit[1]; // behavior
				if (id.equals("Alpha") && action.equals("pickup")) {
					if (f4 == false && f0 == false) {
						f4 = true;
						f0 = true;
						TextMessage textMsg = session.createTextMessage("eat");
						aProducer.send(textMsg);
					} else {
						TextMessage textMsg = session
								.createTextMessage("think");
						aProducer.send(textMsg);
					}
				} else if (id.equals("Bravo") && action.equals("pickup")) {
					if (f0 == false && f1 == false) {
						f0 = true;
						f1 = true;
						TextMessage textMsg = session.createTextMessage("eat");
						;
						bProducer.send(textMsg);
					} else {
						TextMessage textMsg = session
								.createTextMessage("think");
						bProducer.send(textMsg);
					}
				} else if (id.equals("Charlie") && action.equals("pickup")) {
					if (f1 == false && f2 == false) {
						f1 = true;
						f2 = true;
						TextMessage textMsg = session.createTextMessage("eat");
						cProducer.send(textMsg);
					} else {
						TextMessage textMsg = session
								.createTextMessage("think");
						cProducer.send(textMsg);
					}
				} else if (id.equals("Delta") && action.equals("pickup")) {
					if (f2 == false && f3 == false) {
						f2 = true;
						f3 = true;
						TextMessage textMsg = session.createTextMessage("eat");
						dProducer.send(textMsg);
					} else {
						TextMessage textMsg = session
								.createTextMessage("think");
						dProducer.send(textMsg);
					}
				} else if (id.equals("Echo") && action.equals("pickup")) {
					if (f3 == false && f4 == false) {
						f3 = true;
						f4 = true;
						TextMessage textMsg = session.createTextMessage("eat");
						eProducer.send(textMsg);
					} else {
						TextMessage textMsg = session
								.createTextMessage("think");
						eProducer.send(textMsg);
					}
				}

				// drop forks
				if (id.equals("Alpha") && action.equals("drop")) {
					if (f4 == true && f0 == true) {
						f4 = false;
						f0 = false;
					}
				} else if (id.equals("Bravo") && action.equals("drop")) {
					if (f0 == true && f1 == true) {
						f0 = false;
						f1 = false;
					}
				} else if (id.equals("Charlie") && action.equals("drop")) {
					if (f1 == true && f2 == true) {
						f1 = false;
						f2 = false;
					}
				} else if (id.equals("Delta") && action.equals("drop")) {
					if (f2 == true && f3 == true) {
						f2 = false;
						f3 = false;
					}
				} else if (id.equals("Echo") && action.equals("drop")) {
					if (f3 == true && f4 == true) {
						f3 = false;
						f4 = false;
					}
				}
			} else {
				System.out.println("Unexpected message type in waiter.");
			}
		} catch (JMSException jmse) {
			System.err.println("Error in waiter.");
			System.exit(1);
		}
	}
}