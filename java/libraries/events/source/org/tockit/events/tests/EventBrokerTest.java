/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import com.mockobjects.ExpectationCounter;

public class EventBrokerTest extends TestCase {
	public EventBrokerTest(String s) {
		super(s);
	}

	public static Test suite() {
		return new TestSuite(EventBrokerTest.class);
	}

	static class TestListener implements EventBrokerListener<Object> {
		public ExpectationCounter eventCounter = new ExpectationCounter(
				"processEvents()");

		public TestListener() {
			// nothing to do
		}

		public void processEvent(Event<? extends Object> e) {
			eventCounter.inc();
		}
	}

	public void testEventBrokerFiltering() {
		TestListener testListener1 = new TestListener();
		TestListener testListener2 = new TestListener();
		TestListener testListener3 = new TestListener();
		TestListener testListener4 = new TestListener();
		TestListener testListener5 = new TestListener();
		TestListener testListener6 = new TestListener();
		TestListener testListener7 = new TestListener();

		EventBroker<Object> mainBroker = new EventBroker<Object>();
		mainBroker.subscribe(testListener1, TestEventType1.class, Object.class);
		mainBroker
				.subscribe(testListener2, TestEventType1.class, Integer.class);
		mainBroker.subscribe(testListener3, TestEventType2.class, Object.class);
		mainBroker
				.subscribe(testListener4, TestEventType2.class, Integer.class);
		mainBroker.subscribe(testListener5, TestEventType3.class, Object.class);
		mainBroker.subscribe(testListener7, TestEventType1.class, Object.class);
		mainBroker.subscribe(testListener7, TestEventType2.class, Object.class);
		mainBroker.subscribe(testListener7, TestEventType3.class, Object.class);
		mainBroker.removeSubscription(testListener7, TestEventType1b.class,
				Object.class);
		mainBroker.removeSubscription(testListener7, TestEventType2.class,
				Integer.class);
		mainBroker.removeSubscription(testListener7, TestEventType3.class,
				Object.class);

		// create a chain: another broker listens to the main one, catching only
		// 1b events. A listener listens
		// to all type 1 events from Integer sources, i.e. it accepts more
		// events but should get only the 1b ones
		EventBroker<Object> eventType1BBroker = new EventBroker<Object>();
		mainBroker.subscribe(eventType1BBroker, TestEventType1b.class,
				Object.class);
		eventType1BBroker.subscribe(testListener6, TestEventType1.class,
				Integer.class);

		testListener1.eventCounter.setExpected(4);
		testListener2.eventCounter.setExpected(2);
		testListener3.eventCounter.setExpected(2);
		testListener4.eventCounter.setExpected(1);
		testListener5.eventCounter.setExpected(1);
		testListener6.eventCounter.setExpected(1);
		testListener7.eventCounter.setExpected(6);

		mainBroker.processEvent(new TestEventType1(new Object()));
		mainBroker.processEvent(new TestEventType1(new Integer(0)));
		mainBroker.processEvent(new TestEventType1b(new Object()));
		mainBroker.processEvent(new TestEventType1b(new Integer(0)));
		mainBroker.processEvent(new TestEventType2(new Object()));
		mainBroker.processEvent(new TestEventType2(new Integer(0)));
		mainBroker.processEvent(new TestEventType3(new Object()));

		testListener1.eventCounter.verify();
		testListener2.eventCounter.verify();
		testListener3.eventCounter.verify();
		testListener4.eventCounter.verify();
		testListener5.eventCounter.verify();
		testListener6.eventCounter.verify();
		testListener7.eventCounter.verify();
	}
}
