package csr;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class CSRMainFrame extends JFrame {

	private static final long serialVersionUID = -6505639681215242239L;
	
	public CSRMainFrame(){
		super();
		
		
		
	}
	
	protected void processWindowEvent(WindowEvent e) {
	    super.processWindowEvent(e);
	    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
	      System.exit(0);
	    }
	}

}
