package cs455.overlay.transport;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;

public class MessagingNodeConsoleReader implements Runnable {

	private MessagingNode node;
	
	public MessagingNodeConsoleReader(MessagingNode node)
	{
		this.node = node;
	}
	
	@Override
	public void run() {
		try{
		      BufferedReader fromConsole = 
		        new BufferedReader(new InputStreamReader(System.in));
		      String message;

		      while (true) {
		        
		    	message = fromConsole.readLine();
		    	if(message.equals("exit-overlay"))
		    	{
		    		node.exitOverlay();
		    	}
		    	else if(message.equals("print-shortest-path"))
		    	{
		    		node.printShortestPath();
		    	}
		    	else if(message.equals("show-connected-nodes"))
		    	{
		    		node.showConnectedNodes();
		    	}
		    	else if(message.equals("print"))
		    	{
		    		node.printStats();
		    	}
		    	else
		    	{
		    		node.printMessagingNodeCommands();
		    	}
		      }
		    } 
		    catch (Exception ex) {
		      ex.printStackTrace();
		    	System.out.println
		        ("Unexpected error while reading from console!");
		    }		
	}

}
