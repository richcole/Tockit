package docsearcher;


public class GuiThread implements Runnable {
String actionString="";
DocSearch ds;
Thread checker;


GuiThread(DocSearch ds, String actionString) {
this.actionString=actionString;
this.ds=ds;
} // end for constructor

public void start() {
	if(checker == null) {
            checker = new Thread(this, "checker");
            checker.start();
        } // end if null
} // end for start

public void stop() {
	checker.interrupt();
	checker = null;
} // end for stop

public void run() {
 for(Thread thread = Thread.currentThread(); checker == thread;)
	try {
		ds.handleEventCommand(actionString);
		} // end for try
	catch(Exception eR) 
		{ 
		System.out.println("Gui Thread error: "+eR.toString()+"\naction:"+actionString);
		eR.printStackTrace();
		}
	finally {
		stop();
		if (checker!=null) checker.destroy();
		} // end finally
} // end for run



} // end for MetaThread class
