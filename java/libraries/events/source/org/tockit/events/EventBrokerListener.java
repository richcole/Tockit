/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: EventBrokerListener.java,v 1.1.1.1 2002/08/21 05:15:42 peterbecker Exp $
 */
package org.tockit.events;

/**
 * This interface has to be implemented to listen to events.
 *
 * Each object implementing this interface can subscribe to the EventBroker
 * and will get called on processEvent(Event<T>) whenever an event matching the
 * subscription criteria passes the broker.
 * 
 * T is the base type for all subjects we are interested in.
 *
 * @see EventBroker.subscribe(EventBrokerListener, Class, Class)
 */
public interface EventBrokerListener<T> {
    /**
     * The callback for receiving events.
     */
    void processEvent(Event<T> e);
}
