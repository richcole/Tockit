/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tockit.events.filters.EventFilter;
import org.tockit.events.filters.EventTypeFilter;
import org.tockit.events.filters.SubjectTypeFilter;

/**
 * A class distributing events to listeners.
 *
 * This is the central class of the Tockit event processing model. It takes
 * new events on the processEvent(Event) method and distributes them to
 * event listeners that are subscribed to this broker using
 * subscribe(EventBrokerListener, Class, Class).
 *
 * On subscription one can give two possible filter options, one to specify
 * which type of events one is interested in, the other specifies the type
 * of object one cares about. An example of usage would be subscribing to
 * only selection events on rectangles on a canvas.
 *
 * Brokers can subscribe to other brokers since they implement the listener
 * interface. This way a set of event handling contexts can be created where
 * events are passed around. Different components in a system can have their
 * own broker while one could add an additional one on the meta level
 * subscribing to the specific one and thus giving a meta-context where events
 * from different components can be observed.
 *
 * To cause processing of an event one just calls processEvent(Event) with
 * a new object implementing the Event interface. This will put the event at
 * the end of an event queue which will instantly processed. This means events
 * are processed synchronously if they are not enqueued while other events are
 * processed. If you enqueue a new event while another is still being processed
 * (i.e. as reaction on the first event), the new event will be processed after
 * the processing of the first one has finished.
 * 
 * @param <T> The most general type of subjects events can have.
 */
public class EventBroker<T> implements EventBrokerListener<T> {
    private class SubscriptionEvent extends StandardEvent<EventSubscription<T>> {
        public SubscriptionEvent(EventSubscription<T> subject) {
            super(subject);
        }
    }

    private class SubscriptionRemovalEvent extends StandardEvent<EventSubscription<T>> {
        public SubscriptionRemovalEvent(EventSubscription<T> subject) {
            super(subject);
        }
    }

    /**
     * Stores the list of subscriptions.
     */
    private List<EventSubscription<T>> subscriptions = new ArrayList<EventSubscription<T>>();

    /**
     * The event queue.
     */
    private List<Event<? extends T>> eventQueue = new LinkedList<Event<? extends T>>();

    /**
     * The queue for new subscriptions or subscription removals.
     * 
     * Subscriptions can't be changed during event processing (concurrency issues), thus this
     * queue is used to delay the subscriptions. Subscriptions have priority over normal events,
     * which means that a subscription can in theory receive events that happened before the
     * subscription itself. The same applies for removal.
     * 
     * @todo see if we can integrate this back into one queue without breaking type safety
     */
    private List<Event<EventSubscription<T>>> subscriptionQueue = new LinkedList<Event<EventSubscription<T>>>();
    
    /**
     * True if we are already processing some events.
     */
    private boolean processingEvents = false;

    /**
     * Creates a new broker.
     */
    public EventBroker() {
        // nothing to do
    }

    /**
     * Subscribes the listener to a set of specific events.
     *
     * After subscription the listener will receive every event
     * extending or implementing the given eventType (which can
     * be a class or an interface) which involves a subject that
     * extends or implements the given subject type (given as class
     * or interface).
     */
    @SuppressWarnings("unchecked")
	public void subscribe(EventBrokerListener<? super T> listener, Class<? extends Event<? extends T>> eventType, Class<? extends T> subjectType) {
    	subscribe(listener, new EventFilter[]{new EventTypeFilter<Event<? extends T>>(eventType), new SubjectTypeFilter<T,Event<T>>(subjectType)});
    }
    
    public void subscribe(EventBrokerListener<? super T> listener, EventFilter<Event<? extends T>>[] filters) {
    	if (listener == this) {
    		throw new RuntimeException("Trying to subscribe EventBroker to itself");
    	}
    	this.subscriptionQueue.add(new SubscriptionEvent(new EventSubscription<T>(listener, filters)));
    	processEvents();
    }

    /**
     * Removes all subscriptions the listener has.
     *
     * Afterwards the listener will not receive any events anymore.
     */
	public void removeSubscriptions(EventBrokerListener<T> listener) {
		for (Iterator<EventSubscription<T>> iterator = subscriptions.iterator(); iterator.hasNext();) {
			EventSubscription<T> subscription = iterator.next();
			if (subscription.getListener().equals(listener)) {
				this.subscriptionQueue.add(new SubscriptionRemovalEvent(subscription));
			}
		}
		processEvents();
	}

	/**
	 * Removes the given listener from getting events of the specified type.
	 * 
	 * If a subscription of this listener for the given eventType/subjectType is
	 * found it will be removed. This does not check for subclasses or
	 * implemented interfaces, only exact matches will be removed.
	 */
	@SuppressWarnings("unchecked")
	public void removeSubscription(EventBrokerListener<T> listener, Class<? extends Event<T>> eventType, Class<? extends T> subjectType) {
		removeSubscription(new EventSubscription<T>(listener, new EventFilter[]{new EventTypeFilter<Event<T>>(eventType),
																					   new SubjectTypeFilter<T,Event<T>>(subjectType)}));
	}

	
	/**
	 * Deprecated, the first parameter is no longer required.
	 */
	@Deprecated
	public void removeSubscription(@SuppressWarnings("unused") EventBrokerListener<T> listener, 
			                       EventSubscription<T> subscription) {
		removeSubscription(subscription);
	}
	
	public void removeSubscription(EventSubscription<T> subscription) {
		for (Iterator<EventSubscription<T>> iterator = subscriptions.iterator(); iterator.hasNext();) {
			EventSubscription<T> cur = iterator.next();
			if (cur.equals(subscription)) {
				this.subscriptionQueue.add(new SubscriptionRemovalEvent(cur));
			}
		}
		processEvents();
	}

    /**
     * Distributes a new event to the listeners.
     *
     * The given event will be sent to all listeners that are interested in this
     * type of event from the given subject.
     *
     * If the subject of the event (Event.getSource()) is not defined (i.e. null) a
     * RuntimeException will be thrown.
     *
     * @todo check if we really want this limitation, a null subject could just be sent
     *       to all listeners of the event type. This is consistent if one sees null as
     *       universally typed.
     */
    public void processEvent(Event<? extends T> event) {
        if (event.getSubject() == null) {
            throw new RuntimeException("Event needs subject to be processed, null not allowed.");
        }
        this.eventQueue.add(event);
        processEvents();
    }


    /**
     * Processes the current event queues until they are empty.
     */
	private void processEvents() {
        if (processingEvents) {
            return;
        }
        processingEvents = true;
		while (!subscriptionQueue.isEmpty()) {
			Event<EventSubscription<T>> event = subscriptionQueue.remove(0);
            if (event instanceof EventBroker.SubscriptionEvent) {
                this.subscriptions.add(((SubscriptionEvent)event).getSubject());
            } else if (event instanceof EventBroker.SubscriptionRemovalEvent) {
                this.subscriptions.remove(event.getSubject());
            }
        }
        while (!eventQueue.isEmpty()) {
        	processExternalEvent(eventQueue.remove(0));
        }
        processingEvents = false;
    }

    private void processExternalEvent(Event<? extends T> event) {
        for (Iterator<EventSubscription<T>> iterator = subscriptions.iterator(); iterator.hasNext();) {
            EventSubscription<T> subscription = iterator.next();
        	if (subscription.matchesEvent(event)) {
                subscription.getListener().processEvent(event);
            }
        }
    }
}
