package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import cs455.overlay.transport.NodesWithLink;
import cs455.overlay.transport.RegistryConsoleReader;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

public class Registry implements Node{
	
	public ArrayList<MessagingNodeInfo> connectedNodes;
	private int portNumber;
	private ArrayList<Socket> sockets;
	//private ArrayList<TCPSender> senders;
	private ArrayList<TCPReceiver> receivers;
	private Protocol protocol;
	private ArrayList<NodesWithLink> overlayConnections;
	
	public Registry(int portNumber)
	{
		this.connectedNodes = new ArrayList<>();
		this.portNumber = portNumber;
		//this.senders = new ArrayList<>();
		this.receivers = new ArrayList<>();
		this.protocol = new Protocol();
		this.sockets = new ArrayList<>();
		this.overlayConnections = new ArrayList<>();
	}
	
	@Override
	public void onEvent(Event e) {
		// FIXME Auto-generated method stub
		
	}
	
	private void listenForConnections()
	{
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Registry listening on port " + portNumber + "\n");
			while(true)
			{
				Socket socket = serverSocket.accept();
				//System.out.println(socket.getInetAddress().toString());
				TCPReceiver receiver = new TCPReceiver(socket, protocol, this);
				Thread t = new Thread(receiver);
				t.start();
				receivers.add(receiver);
				sockets.add(socket);
			}
		} catch (IOException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void listMessagingNodes()
	{
		for(int x = 0; x < connectedNodes.size(); x++)
		{
			MessagingNodeInfo info = connectedNodes.get(x);
			System.out.println(info);
		}
		System.out.println();
	}
	
	public void sendResponse(byte[] responseArray, Socket socketToSendResponse) throws IOException
	{
		TCPSender sender = new TCPSender(socketToSendResponse);
		sender.sendData(responseArray);
	}
	
	public void setupOverlay(int numberOfConnections)
	{
		for(int x = 0; x < connectedNodes.size(); x++)
		{
			overlayConnections.add(new NodesWithLink(sockets.get(x), sockets.get((x+1)%sockets.size()), 0));
			overlayConnections.add(new NodesWithLink(sockets.get(x), sockets.get((x+2)%sockets.size()), 0));
		}
	}
	
	public static void main(String args[])
	{		
		if(args.length == 1)
		{
			Registry registryNode = new Registry(Integer.parseInt(args[0]));
			Thread t = new Thread(new RegistryConsoleReader(registryNode));
			t.start();
			registryNode.listenForConnections();
		}
		else
		{
			System.out.println("ERROR: Proper usage: java cs455.overlay.node.Registry <portnumber>");
		}
	}
}
