package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import cs455.overlay.dijkstra.DijkstraNode;
import cs455.overlay.dijkstra.NodeWithDistance;
import cs455.overlay.transport.MessagingNodeConsoleReader;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Deregister;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.ReceivedLinkWeights;
import cs455.overlay.wireformats.Register;

public class MessagingNode implements Node{
	
	private Socket socketWithRegistry;
	private ServerSocket serverSocket;
	private ArrayList<TCPReceiver> receivers;
	public int listeningPort;
	public String ipAddress;
	private int port;
	private Protocol protocol;
	TCPReceiver receiverForRegistry;
	public TCPSender senderToRegistry;
	private ArrayList<DijkstraNode> nodesForDijkstras;
	private ArrayList<Integer> connectedPortNumbers;
	public HashMap<String, TCPSender> senders;
	public ArrayList<Socket> receivedSockets;
	public HashMap<String, String> aliasToAddress;
	public HashMap<String, String> addressToAlias;
	public HashMap<String, String> paths;
	public ArrayList<String> nodesEntered;
	public int numMessagesReceived;
	public int numMessagesSent;
	public int summationReceived;
	public int summationSent;
	public int numMessagesRelayed;
	public int receivedForRelay;
	public int sentOnRelay;
	
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
		this.senders = new HashMap<String, TCPSender>();
		
		this.serverSocket = new ServerSocket(0);
		this.listeningPort = serverSocket.getLocalPort();
		System.out.println("Messaging node listening on port " + listeningPort);
		
		Register register = new Register(socketWithRegistry.getLocalAddress().toString(), socketWithRegistry.getLocalPort(), listeningPort);
		byte[] registrationInfo = register.getBytes();
		senderToRegistry.sendData(registrationInfo);
		
		this.nodesForDijkstras = new ArrayList<>();
		this.connectedPortNumbers = new ArrayList<>();
		this.receivedSockets = new ArrayList<>();
		this.aliasToAddress = new HashMap<>();
		this.addressToAlias = new HashMap<>();
		this.nodesEntered = new ArrayList<>();
		this.paths = new HashMap<>();
		
		this.numMessagesReceived = 0;
		this.numMessagesRelayed = 0;
		this.numMessagesSent = 0;
		this.summationReceived = 0;
		this.summationSent = 0;
		this.sentOnRelay = 0;
		this.receivedForRelay = 0;
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
				//System.out.println("ADDRESS FROM REQUESTER:" + socket.getInetAddress().toString() + ":" + socket.getPort());
				//senders.put(socket.getInetAddress().toString() + ":" + socket.getPort(), new TCPSender(socket));
				receivedSockets.add(socket);
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
	
	public void showConnectedNodes()
	{
		
		for (String name: senders.keySet()){

            System.out.println(name); 


		} 
	}
	
	public void addReceiver(Socket receiverSocket) throws IOException
	{
		TCPReceiver newReceiver = new TCPReceiver(this, receiverSocket, protocol);
		Thread t = new Thread(newReceiver);
		t.start();
		this.receivers.add(newReceiver);
	}
	
	public void printShortestPath()
	{
		//TODO: implement
	}
	
	public void printStats()
	{
		System.out.println("Number sent: " + numMessagesSent);
		System.out.println("Number received: " + numMessagesReceived);
		System.out.println("Number relayed: " + numMessagesRelayed);
		System.out.println("Sum received: " + summationReceived);
		System.out.println("Sum sent: " + summationSent);
		System.out.println("Num received for relay: " + receivedForRelay);
		System.err.println("Num sent on relay: " + sentOnRelay);
	}
	
	public void startRounds(int numberOfRounds) throws IOException
	{
		Random rand = new Random();
		for(int x = 0; x < numberOfRounds; x++)
		{
			int randomIndex = rand.nextInt(nodesEntered.size());
			String chosenNode = nodesEntered.get(randomIndex);
			int messageNumber = rand.nextInt();
			String path = paths.get(chosenNode);
			char c = path.charAt(0);
			String firstNodeToSendTo = aliasToAddress.get("" + c);
			//System.out.println("Sending " + messageNumber + " to " + chosenNode + " via " + path);
			//System.out.println("x = " + x);			
			TCPSender firstSender = senders.get(firstNodeToSendTo);
			for(int y = 0; y < 5; y++)
			{
				summationSent += messageNumber;			
				Message m = new Message(path + " " + messageNumber);
				byte[] messageArray = m.getBytes();
				firstSender.sendData(messageArray);
				numMessagesSent++;
			}
		}
	}
	
	public void setUpArrayOfLinks(String[] linksArray) throws IOException
	{
		int counter = 65;
		
		for(int x = 0; x < linksArray.length; x++)
		{
			int linkNum = x+1;
			System.out.println("Linkinfo" + linkNum + " " + linksArray[x]);
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
			
			String node1NameAndPort = node1Name + ":" + node1Port;
			String node2NameAndPort = node2Name + ":" + node2Port;
			
			if(!nodesEntered.contains(node1NameAndPort))
			{
				nodesEntered.add(node1NameAndPort);
				char c = (char)counter;
				aliasToAddress.put("" + c, node1NameAndPort);
				addressToAlias.put(node1NameAndPort, "" + c);
				counter++;
			}
			if(!nodesEntered.contains(node2NameAndPort))
			{
				nodesEntered.add(node2NameAndPort);
				char c = (char)counter;
				aliasToAddress.put("" + c, node2NameAndPort);
				addressToAlias.put(node2NameAndPort, "" + c);
				counter++;
			}
			
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
		nodesEntered.remove(this.ipAddress + ":" + this.listeningPort);
		calculateDijkstras();
		String receivedLinkWeights = "Link weights are received and processed. Ready to send messages.";
		System.out.println(receivedLinkWeights);
		ReceivedLinkWeights receivedWeights = new ReceivedLinkWeights(this.ipAddress.toString() + ":" + this.listeningPort + " - " + receivedLinkWeights);
		byte[] infoToSend = receivedWeights.getBytes();
		senderToRegistry.sendData(infoToSend);
	}
	
	private void calculateDijkstras()
	{		
		//exploredSet has all nodes with the source node (this messaging node) at index 0
		for(int x = 1; x < nodesForDijkstras.size(); x++)
		{			
			ArrayList<DijkstraNode> unexploredSet = getNodesForRunningDijkstras();
			DijkstraNode sourceNode = unexploredSet.get(0);
			int unexploredSetSize = unexploredSet.size();
			ArrayList<DijkstraNode> exploredSet = new ArrayList<>();
			DijkstraNode destinationNode = unexploredSet.get(x);		
			while(unexploredSet.size() != 0)
			{
				Collections.sort(unexploredSet, sortByDistance);
				DijkstraNode workingNode = unexploredSet.remove(0);
				int workingNodeDistance = workingNode.distanceFromSource;
				for(int i = 0; i < workingNode.connections.size(); i++)
				{
					String destNodeName = workingNode.connections.get(i).nodeAddress;
					int destNodePort = workingNode.connections.get(i).nodePortNumber;
					for(int j = 0; j < unexploredSet.size(); j++)
					{
						DijkstraNode nodeToCheck = unexploredSet.get(j);
						if(nodeToCheck.name.equals(destNodeName) && nodeToCheck.portNumber == destNodePort)
						{
							if(nodeToCheck.distanceFromSource > workingNodeDistance + workingNode.connections.get(i).linkWeight)
							{
								nodeToCheck.distanceFromSource = workingNodeDistance + workingNode.connections.get(i).linkWeight;
								nodeToCheck.setParent(workingNode);
							}
						}
					}
				}
				exploredSet.add(workingNode);
			}
			DijkstraNode current = destinationNode;
			String path = "";
			while(!current.equals(sourceNode))
			{
				path = addressToAlias.get(current.toString()) + path;
				current = current.getParent();
			}
			System.out.println(path + " " + destinationNode.distanceFromSource);
			paths.put(destinationNode.toString(), path);
		}
		
		System.out.println("*****ALIAS TO ADDRESS*****");
		for(String name: aliasToAddress.keySet())
		{
			String key = name.toString();
			String value = aliasToAddress.get(name).toString();
			System.out.println(key + " " + value);
		}
		
		System.out.println("*****ADDRESS TO ALIAS*****");
		for(String name: addressToAlias.keySet())
		{
			String key = name.toString();
			String value = addressToAlias.get(name).toString();
			System.out.println(key + " " + value);
		}
		
		System.out.println("*****PATHS*****");
		for(String name: paths.keySet())
		{
			String key = name.toString();
			String value = paths.get(name).toString();
			System.out.println(key + " " + value);
		}
		
		System.out.println("*****SENDERS*****");
		for(String name: senders.keySet())
		{
			String key = name.toString();
			String value = senders.get(name).getSocket().getInetAddress().toString() + ":" + senders.get(name).getSocket().getPort();
			System.out.println("RECVS LISTENING PORT: " + key + " RECVING PORT: " + value);
		}
		
		System.out.println("*****RECEIVERS*****");
		for(int x = 0; x < receivers.size(); x++)
		{
			System.out.println("SENDERS PORT: " + receivers.get(x).getSocket().getInetAddress().toString() + ":" + receivers.get(x).getSocket().getLocalPort());
		}
	}
	
	private ArrayList<DijkstraNode> getNodesForRunningDijkstras()
	{
		ArrayList<DijkstraNode >unexploredSet = new ArrayList<>();			
		DijkstraNode sourceNode = null;
		for(int y = 0; y < nodesForDijkstras.size(); y++)
		{
			DijkstraNode node = new DijkstraNode(nodesForDijkstras.get(y));
			//assign distance of 0 to self
			if(node.name.equals(this.ipAddress) && node.portNumber == this.listeningPort)
			{
				node.distanceFromSource = 0;
				sourceNode = node;
			}
			else
			{
				unexploredSet.add(node);
			}				
		}
		//at this point, unexploredSet has all nodes
		unexploredSet.add(0, sourceNode);
		return unexploredSet;
	}
	
	Comparator<DijkstraNode> sortByDistance = new Comparator<DijkstraNode>() {

		@Override
		public int compare(DijkstraNode o1, DijkstraNode o2) {
			return (int) (o1.distanceFromSource - o2.distanceFromSource);
		}		
	};
	
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
