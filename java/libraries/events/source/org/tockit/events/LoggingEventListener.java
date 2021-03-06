/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events;

import java.io.PrintStream;

public class LoggingEventListener<T> implements EventBrokerListener<T> {
    private PrintStream printStream;

    public LoggingEventListener(EventBroker<T> eventBroker, Class<? extends Event<T>> eventType, Class<T> subjectType, PrintStream printStream) {
        this.printStream = printStream;
        eventBroker.subscribe(this, eventType, subjectType);
    }

    public void processEvent(Event<? extends T> e) {
        printStream.println("Event: " + e.getClass() + "  Subject: " + e.getSubject().getClass());
    }
}
