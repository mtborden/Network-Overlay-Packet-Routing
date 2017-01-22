package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

public class Registry implements Node{
	
	private HashMap<String, Node> nodes;
	private int portNumber;
	private ArrayList<TCPSender> senders;
	private ArrayList<TCPReceiver> receivers;
	private Protocol protocol;
	
	public Registry(int portNumber)
	{
		this.nodes = new HashMap<>();
		this.portNumber = portNumber;
		this.senders = new ArrayList<>();
		this.receivers = new ArrayList<>();
		this.protocol = new Protocol();
	}
	
	@Override
	public void onEvent(Event e) {
		// FIXME Auto-generated method stub
		
	}
	
	private void listenForConnections()
	{
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Registry listening on port " + portNumber);
			while(true)
			{
				Socket socket = serverSocket.accept();
				//System.out.println(socket.getInetAddress().toString());
				TCPReceiver receiver = new TCPReceiver(socket, protocol);
				Thread t = new Thread(receiver);
				t.start();
				senders.add(new TCPSender(socket));
				receivers.add(receiver);
			}
		} catch (IOException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{		
		if(args.length == 1)
		{
			Registry registryNode = new Registry(Integer.parseInt(args[0]));
			registryNode.listenForConnections();
		}
		else
		{
			System.out.println("ERROR: Proper usage: java cs455.overlay.node.Registry <portnumber>");
		}
	}

}
