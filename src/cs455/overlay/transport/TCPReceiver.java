package cs455.overlay.transport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.MessagingNodeInfo;
import cs455.overlay.node.Registry;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.SendPortInfo;
import cs455.overlay.wireformats.SocketInfo;

public class TCPReceiver implements Runnable {

	private Socket socket;
	private DataInputStream inputStream;
	private Protocol protocol;
	private DataOutputStream outputStream;
	private Registry registry;
	private MessagingNode messagingNode;
	
	//private ArrayList<MessagingNode> connectedNodes;
	
	private static final int SUCCESS = 1;
	private static final int FAILURE = 2;
	
	/**
	 * this is the constructor to be used when the TCPReceiver is being created by a MessagingNode
	 * @param socket - the socket on which data is being received
	 * @param protocol - used to determine the type of message being received
	 * @throws IOException
	 */
	public TCPReceiver(MessagingNode node, Socket socket, Protocol protocol) throws IOException{
		this.socket = socket;
		this.inputStream = new DataInputStream(socket.getInputStream());
		this.outputStream = new DataOutputStream(socket.getOutputStream());
		this.protocol = protocol;
		this.messagingNode = node;
	}
	
	/**
	 * this is the constructor to be used when the TCPReceiver is being created by a Registry
	 * @param socket - the socket on which data is being received
	 * @param protocol - used to determine the type of message being received
	 * @param registry - the registry to which this TCPReceiver belongs
	 * @throws IOException
	 */
	public TCPReceiver(Socket socket, Protocol protocol, Registry registry) throws IOException{
		this.socket = socket;
		this.inputStream = new DataInputStream(socket.getInputStream());
		this.outputStream = new DataOutputStream(socket.getOutputStream());
		this.protocol = protocol;
		this.registry = registry;
	}
	
	/**
	 * this is the method that continually checks for incoming messages
	 */
	@Override
	public void run() {
		int dataLength;
		int infoType;
		
		while(socket != null)
		{
			try
			{
				//dataLength = inputStream.readInt();
				unpackBytes(0);				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * unpacks the data being received to be interpreted and displayed correctly
	 * @param type - the type of request being received
	 * @param dataLength - the length in bytes of the data being received
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private void unpackBytes(int dataLength) throws IOException, InterruptedException
	{
		int type = inputStream.readInt();
		int addressLength;
		byte[] address;
		String senderIPAddress;
		int senderPort;
		
		switch(type)
		{
			//registry receiving registration request from messaging node
			case 1:
				//System.out.println("CASE 1");
				addressLength = inputStream.readInt();
				address = new byte[addressLength];
				inputStream.readFully(address, 0, addressLength);
				senderIPAddress = new String(address);
				senderPort = inputStream.readInt();
				int listeningPort = inputStream.readInt();
				System.out.println("Message Type: " + protocol.types.get(type));
				System.out.println("IP Address: " + senderIPAddress);
				System.out.println("Port number: " + listeningPort);
				
				MessagingNodeInfo newNode = new MessagingNodeInfo(senderIPAddress, senderPort, listeningPort);
				if(this.registry.connectedNodes.contains(newNode))
				{
					System.out.println("Request denied. Node already connected.\n");
					String failureReason = "Registration request failure. This node has already registered with the registry.";
					byte[] responseArray = getBytesForRegistrationResponse(FAILURE, null);
					this.registry.sendResponse(responseArray, this.socket);
				}
				else if(!this.socket.getInetAddress().toString().equals(senderIPAddress))
				{
					System.out.println("Request denied. Address mismatch.\n");
					String failureReason = "Registration request failure. There is a mismatch in the address specified and the socket that sent the message.";
					byte[] responseArray = getBytesForRegistrationResponse(FAILURE, failureReason);
					this.registry.sendResponse(responseArray, this.socket);
				}
				else
				{					
					this.registry.connectedNodes.add(newNode);
					this.registry.addressToName.put(senderIPAddress, InetAddress.getByName(senderIPAddress.substring(1)).getHostName());
					System.out.println("Request accepted. There are now " + this.registry.connectedNodes.size() + " nodes connected.\n");
					byte[] responseArray = getBytesForRegistrationResponse(SUCCESS, null);
					this.registry.sendResponse(responseArray, this.socket);
				}
				break;
			//response from registry regarding registration success or failure
			case 2:
				//System.out.println("CASE 2");
				int successOrFailure = inputStream.readInt();
				int messageLength = inputStream.readInt();
				byte[] additionalInfo = new byte[messageLength];
				inputStream.readFully(additionalInfo, 0, messageLength);
				String message = new String(additionalInfo);
				System.out.println("Message type: " + protocol.types.get(type));
				
				//success
				if(successOrFailure == 1)
				{
					System.out.println("Status Code: SUCCESS");
					System.out.println(message);
				}
				//failure
				else
				{
					System.out.println("Status Code: FAILURE");
					System.out.println(message);
				}
				break;
			//registry receiving deregistration request from messaging node
			case 3:
				//System.out.println("CASE 3");
				addressLength = inputStream.readInt();
				address = new byte[addressLength];
				inputStream.readFully(address, 0, addressLength);
				senderIPAddress = new String(address);
				senderPort = inputStream.readInt();
				int listenPort = inputStream.readInt();
				
				System.out.println("Address: " + senderIPAddress + " Port: " + + senderPort);
				
				MessagingNodeInfo nodeInfo = new MessagingNodeInfo(senderIPAddress, senderPort, listenPort);
				boolean contains = false;
				for(int x = 0; x < this.registry.connectedNodes.size(); x++)
				{
					if(this.registry.connectedNodes.get(x).equalsUsingServerSocketPort(nodeInfo));
					contains = true;
				}
				if(!contains)
				{
					System.out.println("Message type: " + protocol.types.get(type));
					System.out.println("Node IP address: " + senderIPAddress);
					System.out.println("Node Port number: "+ nodeInfo.serverSocketPortNumber);
					System.out.println("Request denied. Node was not registered with the registry.\n");
					byte[] responseArray = getBytesForDeregistrationResponse(FAILURE, "Deregistration request failure. This node is not registered with the registry.");
					this.registry.sendResponse(responseArray, this.socket);
				}
				else if(!this.socket.getInetAddress().toString().equals(senderIPAddress))
				{
					System.out.println("Message type: " + protocol.types.get(type));
					System.out.println("Node IP address: " + senderIPAddress);
					System.out.println("Node Port number: "+ nodeInfo.serverSocketPortNumber);
					System.out.println("Request denied. There was a mismatch in the addresses.\n");
					byte[] responseArray = getBytesForDeregistrationResponse(FAILURE, "Deregistration request failure. There was a mismatch in the addresses.");
					this.registry.sendResponse(responseArray, this.socket);
				}
				else
				{
					System.out.println("Message type: " + protocol.types.get(type));
					System.out.println("Node IP address: " + senderIPAddress);
					System.out.println("Node Port number: "+ nodeInfo.serverSocketPortNumber);
					this.registry.connectedNodes.remove(nodeInfo);
					System.out.println("Request accepted. There are now " + this.registry.connectedNodes.size() + " nodes connected.\n");
					byte[] responseArray = getBytesForDeregistrationResponse(SUCCESS, null);
					this.registry.sendResponse(responseArray, this.socket);
					socket = null;
				}
				break;
			//messaging node receiving deregistration response from registry
			case 4:
				//TODO: process deregistration response
				int successful = inputStream.readInt();
				int messLen = inputStream.readInt();
				byte[] messArr = new byte[messLen];
				inputStream.readFully(messArr, 0, messLen);
				String messStr = new String(messArr);
				System.out.println(protocol.types.get(type));
				System.out.println(messStr);
				
				if(successful == 1)
				{
					//successful deregistration					
					//TODO: stop thread, etc.
					this.messagingNode.stopProcess();
					socket = null;
				}
				break;
			//messaging node receiving list of nodes to connect to
			case 5:
				//System.out.println("CASE 5");
				int numberOfNodes = inputStream.readInt();
				int listLength = inputStream.readInt();
				byte[] nodesListArray = new byte[listLength];
				inputStream.readFully(nodesListArray, 0, listLength);
				String nodesList = new String(nodesListArray);
				String[] nodesListStringArray = nodesList.split(" ");
				System.out.println("Message type: " + protocol.types.get(type));
				System.out.println("Number of peer messaging nodes: " + numberOfNodes);
				for(int x = 0; x < nodesListStringArray.length; x++)
				{
					int nodeNum = x+1;
					System.out.println("Messaging node" + nodeNum + ": " + nodesListStringArray[x]);
					String[] addressArray = nodesListStringArray[x].split(":");
					Socket socketToPeer = new Socket(addressArray[0].substring(1), Integer.parseInt(addressArray[1]));
					this.messagingNode.addReceiver(socketToPeer);
					TCPSender sender = new TCPSender(socketToPeer, messagingNode);
					//System.out.println("ADDRESS ON OTHER END: " + socketToPeer.getInetAddress().toString() + ":" + socketToPeer.getPort());
					this.messagingNode.senders.put(socketToPeer.getInetAddress().toString() + ":" + socketToPeer.getPort(), sender);
					SendPortInfo spi = new SendPortInfo(this.messagingNode.listeningPort, socketToPeer.getLocalAddress().toString());
					byte[] info = spi.getBytes();
					sender.sendData(info);
					//TODO: setup all needed construct for connection between peers
					//String socketInfo = socketToPeer.getLocalAddress().toString() + ":" + socketToPeer.getLocalPort() + ";" + socketToPeer.getInetAddress().toString() + ":" + socketToPeer.getPort();
					//System.out.println("SOCKET INFO: "+ socketInfo);
					//this.messagingNode.addConnectedPort(socketToPeer.getLocalPort());
					//System.out.println("SOCKET SETUP: " + socketInfo);
					//SocketInfo socketInformation = new SocketInfo(socketInfo);
					//byte[] infoToSend = socketInformation.getBytes();
					//this.messagingNode.senderToRegistry.sendData(infoToSend);
				}
				System.out.println();
				break;
			//messaging node receiving link info from registry to set up graph/calculate shortest paths
			case 6:
				//System.out.println("CASE 6");
				int numberOfLinks = inputStream.readInt();
				int linksLength = inputStream.readInt();
				byte[] linksListArray = new byte[linksLength];
				inputStream.readFully(linksListArray, 0, linksLength);
				String linksList = new String(linksListArray);
				String[] linksArray = linksList.split(";");
				System.out.println("Message type: " + protocol.types.get(type));
				System.out.println("Number of links: " + numberOfLinks);
				this.messagingNode.setUpArrayOfLinks(linksArray);
				break;
			//registry receiving information about socket that connects two nodes
			case 7:
				//System.out.println("CASE 7");
				int infoLength = inputStream.readInt();
				byte[] socketInformation = new byte[infoLength];
				inputStream.readFully(socketInformation, 0, infoLength);
				System.out.println(new String(socketInformation));
				this.registry.addConnection(new String(socketInformation));
				break;
			//registry receiving confirmation that nodes are ready for rounds
			case 8:
				//System.out.println("CASE 8");
				int messageLen = inputStream.readInt();
				byte[] messageArray = new byte[messageLen];
				inputStream.readFully(messageArray, 0, messageLen);
				System.out.println(new String(messageArray));
				break;
			//messaging node receiving port number info				
			case 9:
				//System.out.println("CASE 9");
				int length = inputStream.readInt();
				byte[] infoArray = new byte[length];				
				inputStream.readFully(infoArray, 0, length);
				String addressToStore = new String(infoArray);
				//int index = this.messagingNode.receivedSockets.indexOf(socket);
				this.messagingNode.senders.put(addressToStore, new TCPSender(socket));
				break;
			//messaging node receiving actual message
			case 10:
				int len = inputStream.readInt();
				byte[] msgArray = new byte[len];
				inputStream.readFully(msgArray, 0, len);
				String pathAndInteger = new String(msgArray);
				//System.out.println("MESSAGE: " + pathAndInteger);
				pathAndInteger = pathAndInteger.substring(1);
				char c = pathAndInteger.charAt(0);
				if(c == ' ')
				{
					//message's final destination, process accordingly
					//System.out.println("FINAL DESTINATION");
					//System.out.println();
					//System.out.println("RECEIVED " + pathAndInteger.substring(1));
					synchronized (this.messagingNode) {
						this.messagingNode.receiveTracker++;
						this.messagingNode.receiveSummation += Long.parseLong(pathAndInteger.substring(1));
					}
					
				}
				else
				{
					//routing node, send to next routing node
					//System.out.println("FORWARDING");
					//System.out.println();					
					String nextIPAddress = this.messagingNode.aliasToAddress.get("" + c);
					//System.out.println("FORWARDING " + pathAndInteger + " TO " + c + "\n");
					Message m = new Message(pathAndInteger);
					byte[] messageToForward = m.getBytes();
					TCPSender senderToNextAddress = this.messagingNode.senders.get(nextIPAddress);
					senderToNextAddress.sendData(messageToForward);
					synchronized (this.messagingNode) {
						//this.messagingNode.receivedForRelay++;
						this.messagingNode.relayTracker++;
						//this.messagingNode.sentOnRelay++;
					}
					
				}
				break;
			//messaging node receiving the task initiate command
			case 11:
				//System.out.println("CASE 11");
				System.out.println("Message Type: " + protocol.types.get(type));
				int numRounds = inputStream.readInt();
				System.out.println("Rounds: " + numRounds);
				this.messagingNode.startRounds(numRounds);
				break;
			//registry receiving TaskComplete info
			case 12:
				int taskCompleteLength = inputStream.readInt();
				byte[] taskCompleteAddressArray = new byte[taskCompleteLength];
				inputStream.readFully(taskCompleteAddressArray, 0, taskCompleteLength);
				System.out.println("Message Type: " + protocol.types.get(type));
				String ipAndPort = new String(taskCompleteAddressArray);
				String[] ipAndPortArray = ipAndPort.split(":");
				if(ipAndPortArray.length == 2)
				{
					System.out.println("Node IP address: " + ipAndPortArray[0]);
					System.out.println("Node Port number: " + ipAndPortArray[1]);
					System.out.println();
					this.registry.numberOfCompletedNodes++;
					if(this.registry.numberOfCompletedNodes == this.registry.connectedNodes.size())
					{
						//send PULL_TRAFFIC_SUMMARY message
						Thread.sleep(15000);
						this.registry.sendPullTrafficMessage();
					}
				}
				else
				{
					System.out.println("ERROR PROCESSING IP AND PORT FOR TASK COMPLETE");
				}
				break;
			//messaging node reciving pull traffic summary message
			case 13:
				this.messagingNode.sendTrafficSummary();
				break;
			//registry receiving summary info from messaging node
			case 14:
				int numberSentLength = inputStream.readInt();
				byte[] numberSentArray = new byte[numberSentLength];
				inputStream.readFully(numberSentArray, 0, numberSentLength);
				String numberSentString = new String(numberSentArray);
				//System.out.println("Number sent: " + numberSentString);
				int numberSent = Integer.parseInt(numberSentString);
				
				int numberReceivedLength = inputStream.readInt();
				byte[] numberReceivedArray = new byte[numberReceivedLength];
				inputStream.readFully(numberReceivedArray, 0, numberReceivedLength);
				String numberReceivedString = new String(numberReceivedArray);
				//System.out.println("Number received: " + numberReceivedString);
				int numberReceived = Integer.parseInt(numberReceivedString);
				
				int numberForwardedLength = inputStream.readInt();
				byte[] numberForwardedArray = new byte[numberForwardedLength];
				inputStream.readFully(numberForwardedArray, 0, numberForwardedLength);
				String numberForwardedString = new String(numberForwardedArray);
				//System.out.println("Number forwarded: " + numberForwardedString);
				int numberForwarded = Integer.parseInt(numberForwardedString);
				
				int summationSentLength = inputStream.readInt();
				byte[] summationSentArray = new byte[summationSentLength];
				inputStream.readFully(summationSentArray, 0, summationSentLength);
				String summationSentString = new String(summationSentArray);
				//System.out.println("Sum sent: " + summationSentString);
				long summationSent = Long.parseLong(summationSentString);
				
				int summationReceivedLength = inputStream.readInt();
				byte[] summationReceivedArray = new byte[summationReceivedLength];
				inputStream.readFully(summationReceivedArray, 0, summationReceivedLength);
				String summationReceivedString = new String(summationReceivedArray);
				//System.out.println("Sum received: " + summationReceivedString);
				long summationReceived = Long.parseLong(summationReceivedString);
				
				NodeSummation ns = new NodeSummation(numberSent, numberReceived, numberForwarded, summationReceived, summationSent);
				synchronized (this.registry.summations) 
				{
					this.registry.summations.add(ns);
				}
				if(this.registry.summations.size() == this.registry.connectedNodes.size())
				{
					this.registry.printSummary();
				}
				break;
			default:
				System.out.println("DEFAULT CASE: " + type);
				break;
		}
	}
	
	private byte[] getBytesForDeregistrationResponse(int responseType, String reasonForFailure) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
		
		//success on deregistration
		if(responseType == 1)
		{
			//write 4 for message type(DEREGISTRATIOIN_RESPONSE)
			outputStream.writeInt(4);
			
			//write 1 for success
			outputStream.writeInt(1);
			
			String message = "Deregistration request successful. This node is now deregistered from the registry.";
			
			byte[] additionalInfo = message.getBytes();
			int additionalInfoLength = additionalInfo.length;
			
			outputStream.writeInt(additionalInfoLength);
			outputStream.write(additionalInfo);
			
			outputStream.flush();
			marshalledBytes = byteArrayOutputStream.toByteArray();
			
			byteArrayOutputStream.close();
			outputStream.close();
		}
		//failure on deregistration
		else
		{
			//write 4 for message type(DEREGISTRATION_RESPONSE)
			outputStream.writeInt(4);
			
			//write 2 for failure
			outputStream.writeInt(2);
			
			byte[] additionalInfo = reasonForFailure.getBytes();
			int additionalInfoLength = additionalInfo.length;
			
			outputStream.writeInt(additionalInfoLength);
			outputStream.write(additionalInfo);
			
			outputStream.flush();
			marshalledBytes = byteArrayOutputStream.toByteArray();
			
			byteArrayOutputStream.close();
			outputStream.close();
		}
		
		return marshalledBytes;
	}
	
	private byte[] getBytesForRegistrationResponse(int responseType, String reasonForFailure) throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
		
		//success on registration
		if(responseType == 1)
		{
			//write 2 for message type (REGISTRATION_RESPONSE)
			outputStream.writeInt(2);
			
			//write 1 for success
			outputStream.writeInt(1);
			
			String message = "Registration request successful. The number of messaging nodes currently constituting the overlay is " + registry.connectedNodes.size() + ".\n";
			
			byte[] additionalInfo = message.getBytes();
			int additionalInfoLength = additionalInfo.length;
			
			outputStream.writeInt(additionalInfoLength);
			outputStream.write(additionalInfo);
			
			outputStream.flush();
			marshalledBytes = byteArrayOutputStream.toByteArray();
			
			byteArrayOutputStream.close();
			outputStream.close();			
		}
		//failure on registration
		else
		{
			//write 2 for message type (REGISTRATION_RESPONSE)
			outputStream.writeInt(2);
			
			//write 2 for failure
			outputStream.writeInt(2);
			
			byte[] additionalInfo = reasonForFailure.getBytes();
			int additionalInfoLength = additionalInfo.length;
			
			outputStream.writeInt(additionalInfoLength);
			outputStream.write(additionalInfo);
			
			outputStream.flush();
			marshalledBytes = byteArrayOutputStream.toByteArray();
			
			byteArrayOutputStream.close();
			outputStream.close();
		}
		
		return marshalledBytes;
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
}
