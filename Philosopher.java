//  Parker Combs
//  parker.combs@und.edu
//  CSCI 364
//  Assignment 4
package philosopher;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import waiter.Waiter;

public class Philosopher implements Runnable {
	private String name; // identifier for Philosopher
	private long thoughts; // total loop iterations
	private long meals; // total number of times a philosopher eats
	private long critTime; // accumulated time spent waiting for the waiter or
							// processing the critical section
	private long critTempTime;
	private long totalTime; // total time spent in the run method
	private Waiter arbitrator;
	private boolean stopLoop;
	private int decision; // 0 = eat, 1 = not eat
	private long beginTime;
	private Connection connection;
	private Session session;
	private Destination outQueue;
	private Destination inQueue;
	private MessageProducer producer;
	private MessageConsumer consumer;

	public Philosopher(Waiter waiter, String id, Connection conn) {
		arbitrator = waiter;
		name = id;
		thoughts = 0;
		meals = 0;
		critTime = 0;
		critTempTime = 0;
		totalTime = 0;
		stopLoop = false;
		decision = -1;
		beginTime = 0;
		try {
			connection = conn;
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			outQueue = session.createQueue("hw4.queue.Waiter");
			inQueue = session.createQueue("hw4.queue." + name);
			producer = session.createProducer(outQueue); // outQueue ==
															// hw4.queue.Waiter,
															// producer -
															// sends to this
			consumer = session.createConsumer(inQueue); // inQueue ==
														// hw4.queue.[philosopher_name],
														// consumer -
														// receives from
														// this
		} catch (JMSException jmse) {
			System.err.println("Error initializing philosopher.");
			System.exit(1);
		}
	}

	public void run() {
		beginTime = System.currentTimeMillis();
		stopLoop = false;
		decision = -1;
		while (stopLoop != true) {
			thoughts++;
			try {
				TextMessage textMessage = session.createTextMessage(this
						.getName() + " pickup");
				critTempTime = System.currentTimeMillis();
				producer.send(textMessage);
				arbitrator.onMessage(textMessage);
				Message msgReceive = consumer.receive(100);
				critTime = critTime
						+ (System.currentTimeMillis() - critTempTime);
				if (msgReceive instanceof TextMessage) {
					TextMessage tmReceive = (TextMessage) msgReceive;
					String strReceive = tmReceive.getText();
					if (strReceive.equals("eat")) {
						decision = 0;
						TextMessage dropForks = session.createTextMessage(this
								.getName() + " drop");
						producer.send(dropForks);
						arbitrator.onMessage(dropForks);
					} else if (strReceive.equals("think")) {
						decision = 1;
					}
				} else {
					System.out.println("Unknown message type in philosopher. "
							+ name);
				}

			} catch (JMSException jmse) {
				System.err.println("Error running thread. " + name);
				System.exit(1);
			}
			if (decision == 0) {
				meals++;
				decision = 1;
			} else if (decision == 1) {
				thoughts++;
				decision = 1;
			}
		}
		totalTime = (System.currentTimeMillis() - beginTime);
	}

	public String getName() {
		return name;
	}

	public long getMeals() {
		return meals;
	}

	public long getThoughts() {
		return thoughts;
	}

	public long getCritTime() {
		return critTime;
	}

	public void setCritTime(long temp) {
		critTime = temp;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void StopLoop() {
		stopLoop = true;
	}
}
