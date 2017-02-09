package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cs455.overlay.dijkstra.Connection;
import cs455.overlay.transport.NodeSummation;
import cs455.overlay.transport.NodesWithLink;
import cs455.overlay.transport.RegistryConsoleReader;
import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.PullTrafficSummary;
import cs455.overlay.wireformats.TaskInitiate;

public class Registry implements Node{
	
	public ArrayList<MessagingNodeInfo> connectedNodes;
	private int portNumber;
	private ArrayList<Socket> sockets;
	private ArrayList<TCPSender> senders;
	private ArrayList<TCPReceiver> receivers;
	private Protocol protocol;
	public ArrayList<NodesWithLink> overlayConnections;
	public ArrayList<Connection> connections;
	private Random rand;
	public int numReadyNodes;
	public int numberOfCompletedNodes;
	public ArrayList<NodeSummation> summations;
	
	public Registry(int portNumber)
	{
		this.connectedNodes = new ArrayList<>();
		this.portNumber = portNumber;
		this.senders = new ArrayList<>();
		this.receivers = new ArrayList<>();
		this.protocol = new Protocol();
		this.sockets = new ArrayList<>();
		this.overlayConnections = new ArrayList<>();
		this.connections = new ArrayList<>();
		this.rand = new Random();
		this.numReadyNodes = 0;
		this.numberOfCompletedNodes = 0;
		this.summations = new ArrayList<>();
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
				//System.out.println("*******" + socket.getInetAddress().getHostAddress());
				TCPReceiver receiver = new TCPReceiver(socket, protocol, this);
				Thread t = new Thread(receiver);
				t.start();
				receivers.add(receiver);
				sockets.add(socket);
				senders.add(new TCPSender(socket));
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
	
	public void startRounds(int numberOfRounds) throws IOException
	{
		TaskInitiate t = new TaskInitiate(numberOfRounds);
		byte[] messageToSend = t.getBytes();
		for(int x = 0; x < senders.size(); x++)
		{
			senders.get(x).sendData(messageToSend);
		}
	}
	
	public void sendPullTrafficMessage() throws IOException
	{
		PullTrafficSummary pts = new PullTrafficSummary();
		byte[] messageToSend = pts.getBytes();
		for(int x = 0; x < senders.size(); x++)
		{
			senders.get(x).sendData(messageToSend);
		}
	}
	
	public void printSummary()
	{
		System.out.println("\tNumber\tNumber of\tSummation of sent\tSummation of received\tNumber of");
		System.out.println("\tof\tmessages\tmessages\tmessages\tmessages relayed");
		System.out.println("\tmessages\treceived");
		System.out.println("\tsent");
		int totalSent = 0;
		int totalReceived = 0;
		int sumSent = 0;
		int sumReceived = 0;
		int totalForwarded = 0;
		for(int x = 0; x < summations.size(); x++)
		{
			NodeSummation ns = summations.get(x);
			int nodeNum = x+1;
			System.out.println("Node " + nodeNum + "\t" + ns.numberSent + "\t" + ns.numberReceived + "\t" + ns.summationSent + "\t" + ns.summationSent + "\t" + ns.numberForwarded);
			totalSent += ns.numberSent;
			totalReceived += ns.numberReceived;
			totalForwarded += ns.numberForwarded;
			sumSent += ns.summationSent;
			sumReceived += ns.summationReceived;
		}
		System.out.println("Sum\t" + totalSent + "\t" + totalReceived + "\t" + sumSent + "\t" + sumReceived + "\t" + totalForwarded);
	}
	
	public void listWeights()
	{
		for(int x = 0; x < overlayConnections.size(); x++)
		{
			NodesWithLink info = overlayConnections.get(x);
			System.out.println(info.getFirstNode().ipAddress + ":" + info.getFirstNode().serverSocketPortNumber + " " + info.getSecondNode().ipAddress + ":" + info.getSecondNode().serverSocketPortNumber + " " + info.getWeight());
		}
		System.out.println();
	}
	
	public void sendResponse(byte[] responseArray, Socket socketToSendResponse) throws IOException
	{
		TCPSender sender = new TCPSender(socketToSendResponse);
		sender.sendData(responseArray);
	}
	
	/**
	 * Sends information to each MessagingNode about which nodes it should connect to
	 * @param numberOfConnections - disregard
	 * @throws IOException
	 */
	public void setupOverlay(int numberOfConnections) throws IOException
	{
		for(int x = 0; x < connectedNodes.size(); x++)
		{
			overlayConnections.add(new NodesWithLink(connectedNodes.get(x), connectedNodes.get((x+1)%connectedNodes.size()), 0));
			overlayConnections.add(new NodesWithLink(connectedNodes.get(x), connectedNodes.get((x+2)%connectedNodes.size()), 0));
		}
		
		Random rand = new Random();
		
		for(int x = 0; x < overlayConnections.size(); x++)
		{
			NodesWithLink link = overlayConnections.get(x);
			int randomWeight = rand.nextInt(10);
			randomWeight++;
			link.setWeight(randomWeight);
		}
		
		sendMessagingNodes();
	}
	
	private void sendMessagingNodes() throws IOException
	{		
		for(int x = 0; x < connectedNodes.size(); x++)
		{
			MessagingNodesList list = new MessagingNodesList(this, 5, connectedNodes.get(x));
			byte[] message = list.getBytes();
			
			//TODO: find corresponding socket to connectedNodes(x) and send to that
			for(int y = 0; y < sockets.size(); y++)
			{
				Socket s = sockets.get(y);
				if(s.getInetAddress().toString().equals(connectedNodes.get(x).ipAddress) && s.getPort() == connectedNodes.get(x).portNumber)
				{
					sendResponse(message, sockets.get(y));
				}
			}			
		}
	}
	
	public String getLinks()
	{
		String links = "";
		for(int x = 0; x < overlayConnections.size(); x++)
		{
			links = links + overlayConnections.get(x).toString() + ";";			
		}
		return links;
	}
	
	public void sendLinkInfo() throws IOException
	{
		System.out.println();
		LinkWeights linkWeights = new LinkWeights(6, this);
		byte[] message = linkWeights.getBytes();
		
		for(int x = 0; x < sockets.size(); x++)
		{			
			sendResponse(message, sockets.get(x));
		}
		/*for(int x = 0; x < connectedNodes.size(); x++)
		{
			String nodeToSendInfoAddress = connectedNodes.get(x).ipAddress;
			int nodeToSendInfoPort = connectedNodes.get(x).portNumber;
			
			for(int y = 0; y < overlayConnections.size(); y++)
			{
				NodesWithLink link = overlayConnections.get(y);
				if(link.getFirstNode().ipAddress.equals(nodeToSendInfoAddress) && link.getFirstNode().portNumber == nodeToSendInfoPort)
				{
					//TODO: send the link info					
				}
			}
		}*/
	}
	
	/**
	 * Gets the nodes to which the requesting node should attempt to connect
	 * @param info - information about the node that is requesting the information
	 * @return the list of nodes to which it should connect
	 */
	public String getMessagingNodes(MessagingNodeInfo info)
	{
		String nodes = "";
		int numberOfPeerNodes = 0;
		for(int x = 0; x < overlayConnections.size(); x++)
		{
			NodesWithLink link = overlayConnections.get(x);			
			if(link.getFirstNode().equals(info))
			{
				nodes += (link.getSecondNode().ipAddress + ":" + link.getSecondNode().serverSocketPortNumber + " ");
				numberOfPeerNodes++;
			}
		}
		
		return numberOfPeerNodes + nodes;
	}
	
	public void addConnection(String linkInfo)
	{
		String[] twoNodes = linkInfo.split(";");
		if(twoNodes.length == 2)
		{
			int connectionWeight = rand.nextInt(10);
			connectionWeight++;
			connections.add(new Connection(twoNodes[0], twoNodes[1], connectionWeight));
			System.out.println("Link added");
		}
		else
		{
			System.out.println("Link information incorrect.");
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
