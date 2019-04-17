//  Parker Combs										
//  parker.combs@und.edu
//  CSCI 364
//  Assignment 4

//  This is a program that implements a non-deadlocking solution to the dining philosophers problem by emulating the behavior of actors using MessageListener.
//                              A  
//                    ______________________
//             E     /                      \     B
//                   |   F4            F0   |
//                   |                      |
//                   |          _           |
//                   |         |_|          |
//                   |                      |
//                   |   F3            F1   |
//                   |                      |
//                   |          F2          |
//                   \______________________/
//             D                                  C
package diner;

import java.lang.InterruptedException;
import java.lang.NumberFormatException;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

import philosopher.Philosopher;

import waiter.Waiter;

public class Diner {

	public static void main(String[] args) {
		// check if valid wait time
		if (args.length != 1) {
			System.out.println("Error. Usage: \"java Diner <wait time>\".");
			System.exit(1);
		}
		try {
			Long.parseLong(args[0]);
		} catch (NumberFormatException nfe) {
			System.err
					.println("Error. Cannot parse argument as a long number.");
			System.exit(1);
		}
		long sleepTime = Long.parseLong(args[0]);
		if (sleepTime < 0) {
			System.out
					.println("Error. Cannot wait for a negative amount of time.");
			System.exit(1);
		}
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				"tcp://localhost:61616");
		System.out.println("Welcome to the Philosophers' Diner!\n");
		Connection connectionA = null;
		Connection connectionB = null;
		Connection connectionC = null;
		Connection connectionD = null;
		Connection connectionE = null;
		Connection connectionW = null;
		Philosopher alpha = null;
		Philosopher bravo = null;
		Philosopher charlie = null;
		Philosopher delta = null;
		Philosopher echo = null;
		Waiter waiter = null;
		try {
			connectionA = factory.createConnection();
			connectionB = factory.createConnection();
			connectionC = factory.createConnection();
			connectionD = factory.createConnection();
			connectionE = factory.createConnection();
			connectionW = factory.createConnection();
			waiter = new Waiter(connectionW);
			alpha = new Philosopher(waiter, "Alpha", connectionA);
			bravo = new Philosopher(waiter, "Bravo", connectionB);
			charlie = new Philosopher(waiter, "Charlie", connectionC);
			delta = new Philosopher(waiter, "Delta", connectionD);
			echo = new Philosopher(waiter, "Echo", connectionE);

			connectionW.start();
			connectionA.start();
			connectionB.start();
			connectionC.start();
			connectionD.start();
			connectionE.start();

		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}

		Thread aThread = new Thread(alpha);
		Thread bThread = new Thread(bravo);
		Thread cThread = new Thread(charlie);
		Thread dThread = new Thread(delta);
		Thread eThread = new Thread(echo);

		aThread.start();
		bThread.start();
		cThread.start();
		dThread.start();
		eThread.start();
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException ie) {
			System.out.println("Error. Thread interrupted.");
			System.exit(1);
		}
		try {
			alpha.StopLoop();
			bravo.StopLoop();
			charlie.StopLoop();
			delta.StopLoop();
			echo.StopLoop();

			aThread.join();
			bThread.join();
			cThread.join();
			dThread.join();
			eThread.join();
		} catch (InterruptedException ie) {
			System.out.println("Error. Thread interrupted.");
			System.exit(1);
		}

		try {
			connectionW.close();
			connectionA.close();
			connectionB.close();
			connectionC.close();
			connectionD.close();
			connectionE.close();
		} catch (JMSException jmse) {
			System.err.println("Error when closing main connection.");
			System.exit(1);
		}

		System.out.println("\nThe diner is closed.");

		// Alpha's Data
		System.out.println("Philosopher: " + alpha.getName());
		System.out.printf("\tThoughts: %d, Meals: %d", alpha.getThoughts(),
				alpha.getMeals());
		System.out.printf("\n\tCritical section time (ms): %d",
				alpha.getCritTime());
		System.out.printf("\n\tTotal time (ms): %d", alpha.getTotalTime());
		System.out.printf("\n\tTime Ratio -- Critical section / Total: %f\n",
				((float) alpha.getCritTime() / (float) alpha.getTotalTime()));

		// Bravo's Data
		System.out.println("Philosopher: " + bravo.getName());
		System.out.printf("\tThoughts: %d, Meals: %d", bravo.getThoughts(),
				bravo.getMeals());
		System.out.printf("\n\tCritical section time (ms): %d",
				bravo.getCritTime());
		System.out.printf("\n\tTotal time (ms): %d", bravo.getTotalTime());
		System.out.printf("\n\tTime Ratio -- Critical section / Total: %f\n",
				((float) bravo.getCritTime() / (float) bravo.getTotalTime()));

		// Charlie's Data
		System.out.println("Philosopher: " + charlie.getName());
		System.out.printf("\tThoughts: %d, Meals: %d", charlie.getThoughts(),
				charlie.getMeals());
		System.out.printf("\n\tCritical section time (ms): %d",
				charlie.getCritTime());
		System.out.printf("\n\tTotal time (ms): %d", charlie.getTotalTime());
		System.out
				.printf("\n\tTime Ratio -- Critical section / Total: %f\n",
						((float) charlie.getCritTime() / (float) charlie
								.getTotalTime()));

		// Delta's Data
		System.out.println("Philosopher: " + delta.getName());
		System.out.printf("\tThoughts: %d, Meals: %d", delta.getThoughts(),
				delta.getMeals());
		System.out.printf("\n\tCritical section time (ms): %d",
				delta.getCritTime());
		System.out.printf("\n\tTotal time (ms): %d", delta.getTotalTime());
		System.out.printf("\n\tTime Ratio -- Critical section / Total: %f\n",
				((float) delta.getCritTime() / (float) delta.getTotalTime()));

		// Echo's Data
		System.out.println("Philosopher: " + echo.getName());
		System.out.printf("\tThoughts: %d, Meals: %d", echo.getThoughts(),
				echo.getMeals());
		System.out.printf("\n\tCritical section time (ms): %d",
				echo.getCritTime());
		System.out.printf("\n\tTotal time (ms): %d", echo.getTotalTime());
		System.out.printf("\n\tTime Ratio -- Critical section / Total: %f\n",
				((float) echo.getCritTime() / (float) echo.getTotalTime()));
	}
}