package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cs455.overlay.dijkstra.DijkstraNode;
import cs455.overlay.dijkstra.NodeWithDistance;
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
	public int listeningPort;
	private String ipAddress;
	private int port;
	private Protocol protocol;
	TCPReceiver receiverForRegistry;
	public TCPSender senderToRegistry;
	private ArrayList<DijkstraNode> nodesForDijkstras;
	private ArrayList<Integer> connectedPortNumbers;
	
	/**
	 * Constructor for MessagingNode. Sets up socket with the registry, and starts that thread, starts up server socket and starts listening for incoming connections. Automatically registers with the registry
	 * @param hostName - registry's ip address or name, first argument from console
	 * @param portNumber - registry's port number, second argument from console
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public MessagingNode(String hostName, int portNumber) throws UnknownHostException, IOException
	{
		this.socketWithRegistry = new Socket(hostName, portNumber);
		this.ipAddress = socketWithRegistry.getLocalAddress().toString();
		this.port = socketWithRegistry.getLocalPort();
		this.protocol = new Protocol();
		senderToRegistry = new TCPSender(socketWithRegistry);
		receiverForRegistry = new TCPReceiver(this, socketWithRegistry, protocol);
		Thread t = new Thread(receiverForRegistry);
		t.start();
		
		this.receivers = new ArrayList<>();
		this.receivers.add(receiverForRegistry);
		this.senders = new ArrayList<>();
		
		this.serverSocket = new ServerSocket(0);
		this.listeningPort = serverSocket.getLocalPort();
		System.out.println("Messaging node listening on port " + listeningPort);
		
		Register register = new Register(socketWithRegistry.getLocalAddress().toString(), socketWithRegistry.getLocalPort(), listeningPort);
		byte[] registrationInfo = register.getBytes();
		senderToRegistry.sendData(registrationInfo);
		
		this.nodesForDijkstras = new ArrayList<>();
		this.connectedPortNumbers = new ArrayList<>();
	}
	
	@Override
	public void onEvent(Event e) {
		// FIXME Auto-generated method stub
		
	}
	
	/**
	 * Continually listens for incoming connections. Sets up TCPReceiver to listen once connection is established
	 */
	private void listenForConnections()
	{
		try {		
			
			while(true)
			{
				Socket socket = serverSocket.accept();
				//System.out.println("GOT REQUEST FROM PEER");
				addConnectedPort(socket.getLocalPort());
				senders.add(new TCPSender(socket));
				TCPReceiver receiver = new TCPReceiver(this, socket, protocol);
				Thread t = new Thread(receiver);
				t.start();
				receivers.add(receiver);
			}
		} catch (IOException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Deregisters the MessagingNode with the Registry
	 * @throws IOException
	 */
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
	
	public void setUpArrayOfLinks(String[] linksArray)
	{
		for(int x = 0; x < linksArray.length; x++)
		{
			System.out.println(linksArray[x]);
			String link = linksArray[x];
			String[] linkArray = link.split(" ");
			String node1 = linkArray[0];
			String[] node1Array = node1.split(":");
			String node1Name = node1Array[0];
			int node1Port = Integer.parseInt(node1Array[1]);
			String node2 = linkArray[1];
			String[] node2Array = node2.split(":");
			String node2Name = node2Array[0];
			int node2Port = Integer.parseInt(node2Array[1]);
			int linkWeight = Integer.parseInt(linkArray[2]);
			DijkstraNode nodeToAdd1 = new DijkstraNode(node1Name, node1Port);
			DijkstraNode nodeToAdd2 = new DijkstraNode(node2Name, node2Port);			
			
			if(!nodesForDijkstras.contains(nodeToAdd1))
			{
				nodeToAdd1.connections.add(new NodeWithDistance(node2Name, node2Port, linkWeight));
				nodesForDijkstras.add(nodeToAdd1);
			}
			else
			{
				int index = nodesForDijkstras.indexOf(nodeToAdd1);
				DijkstraNode nodeToAddConnectionTo = nodesForDijkstras.get(index);
				nodeToAddConnectionTo.connections.add(new NodeWithDistance(node2Name, node2Port, linkWeight));
			}
			if(!nodesForDijkstras.contains(nodeToAdd2))
			{
				nodeToAdd2.connections.add(new NodeWithDistance(node1Name, node1Port, linkWeight));
				nodesForDijkstras.add(nodeToAdd2);
			}		
			else
			{
				int index = nodesForDijkstras.indexOf(nodeToAdd2);
				DijkstraNode nodeToAddConnectionTo = nodesForDijkstras.get(index);
				nodeToAddConnectionTo.connections.add(new NodeWithDistance(node1Name, node1Port, linkWeight));
			}
		}
		for(int x = 0; x < nodesForDijkstras.size(); x++)
		{
			System.out.println(nodesForDijkstras.get(x).name + ":" + nodesForDijkstras.get(x).portNumber);
		}
	}
	
	/**
	 * adds the specified port to the list of connected ports - used in calculating Dijkstra's algorithm
	 * @param connectedPort - the port to add
	 */
	public void addConnectedPort(int connectedPort)
	{
		this.connectedPortNumbers.add(connectedPort);
	}
	
	/**
	 * Prints out if an unrecognized command is entered
	 */
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
