package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cs455.overlay.transport.MessagingNodeConsoleReader;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Deregister;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.Register;

public class MessagingNode implements Node{
	
	private Socket socketWithRegistry;
	private ServerSocket serverSocket;
	private ArrayList<TCPReceiver> receivers;
	private ArrayList<TCPSender> senders;
	private int listeningPort;
	private String ipAddress;
	private int port;
	private Protocol protocol;
	TCPReceiver receiverForRegistry;
	TCPSender senderToRegistry;
	
	public MessagingNode(String hostName, int portNumber) throws UnknownHostException, IOException
	{
		this.socketWithRegistry = new Socket(hostName, portNumber);
		this.ipAddress = socketWithRegistry.getLocalAddress().toString();
		this.port = socketWithRegistry.getLocalPort();
		this.protocol = new Protocol();
		senderToRegistry = new TCPSender(socketWithRegistry);
		receiverForRegistry = new TCPReceiver(socketWithRegistry, protocol);
		Thread t = new Thread(receiverForRegistry);
		t.start();
		Register register = new Register(socketWithRegistry.getLocalAddress().toString(), socketWithRegistry.getLocalPort());
		byte[] registrationInfo = register.getBytes();
		senderToRegistry.sendData(registrationInfo);
		this.receivers = new ArrayList<>();
		this.receivers.add(receiverForRegistry);
		this.senders = new ArrayList<>();		
	}
	
	@Override
	public void onEvent(Event e) {
		// FIXME Auto-generated method stub
		
	}
	
	private void listenForConnections()
	{
		try {
			this.serverSocket = new ServerSocket(0);
			this.listeningPort = serverSocket.getLocalPort();
			System.out.println("Messaging node listening on port " + listeningPort);
			
			while(true)
			{
				Socket socket = serverSocket.accept();
				senders.add(new TCPSender(socket));
				TCPReceiver receiver = new TCPReceiver(socket, protocol);
				Thread t = new Thread(receiver);
				t.start();
				receivers.add(receiver);
			}
		} catch (IOException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exitOverlay() throws IOException
	{
		Deregister deregister = new Deregister(socketWithRegistry.getLocalAddress().toString(), socketWithRegistry.getLocalPort());
		byte[] registrationInfo = deregister.getBytes();
		senderToRegistry.sendData(registrationInfo);
	}
	
	public void printShortestPath()
	{
		//TODO: implement
	}
	
	public void printMessagingNodeCommands()
	{
		System.out.println("That command is not recognized. Please try one of the following commands.");
		System.out.println("print-shortest-paths");
		System.out.println("\tPrints the shortest paths to all other messaging nodes in the system.");
		System.out.println("exit-overlay");
		System.out.println("\tDeregisters with the registry and exits the overlay.");
	}
	
	/**
	 * used to determine if it is the correct node by checking address and port number
	 * @param address - inet address to check against
	 * @param portNumber - port number to check against
	 * @return true if address and port number are equal, false otherwise
	 */
	public boolean equals(String address, int portNumber)
	{
		if(this.socketWithRegistry.getLocalAddress().toString().equals(address) && this.socketWithRegistry.getLocalPort() == portNumber)
		{
			return true;
		}
		return false;
	}
	
	public static void main(String args[]) throws NumberFormatException, UnknownHostException, IOException
	{
		if(args.length == 2)
		{
			MessagingNode messagingNode = new MessagingNode(args[0], Integer.parseInt(args[1]));
			Thread t = new Thread(new MessagingNodeConsoleReader(messagingNode));
			t.start();
			messagingNode.listenForConnections();			
		}
		else
		{
			System.out.println("Proper usage: java cs455.overlay.node.MessagingNode <registry-host> <registry-port>");
		}
	}

}
