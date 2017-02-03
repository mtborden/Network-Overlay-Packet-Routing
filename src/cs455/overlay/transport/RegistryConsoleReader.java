package cs455.overlay.transport;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;

public class RegistryConsoleReader implements Runnable{

private Registry registry;
	
	public RegistryConsoleReader(Registry registry)
	{
		this.registry = registry;
	}
	
	@Override
	public void run() {
		try{
		      BufferedReader fromConsole = 
		        new BufferedReader(new InputStreamReader(System.in));
		      String message;

		      while (true) {
		        
		    	message = fromConsole.readLine();
		    	if(message.equals("list-messaging nodes"))
		    	{
		    		registry.listMessagingNodes();
		    	}
		    	else if(message.equals("list-weights"))
		    	{
		    		registry.listWeights();
		    	}
		    	else if(message.startsWith("setup-overlay"))
		    	{
		    		String[] messageArray = message.split(" ");
		    		if(messageArray.length != 2)
		    		{
		    			System.out.println("Proper usage: setup-overlay <number of connections>.\nPlease try again.");
		    		}
		    		else
		    		{
		    			int numConnections = Integer.parseInt(messageArray[1]);
			    		if(numConnections < 4)
			    		{
			    			System.out.println("The number of connections must be at least 4. Please try again.");
			    		}
			    		else
			    		{
			    			registry.setupOverlay(numConnections);
			    		}
		    		}
		    	}
		    	else if(message.equals("send-overlay-link-weights"))
		    	{
		    		registry.sendLinkInfo();
		    	}
		    	else
		    	{
		    		//TODO: create menu for commands
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
