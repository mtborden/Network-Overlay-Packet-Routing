package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import cs455.overlay.transport.TCPReceiver;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Register;

public class MessagingNode implements Node{
	
	private Socket socket;
	private ServerSocket serverSocket;
	private ArrayList<TCPReceiver> receivers;
	private ArrayList<TCPSender> senders;
	private int receivingPort;
	
	public MessagingNode(String hostName, int portNumber) throws UnknownHostException, IOException
	{
		this.socket = new Socket(hostName, portNumber);
		TCPSender senderToRegistry = new TCPSender(socket);
		Register register = new Register(socket.getLocalAddress().toString(), socket.getLocalPort());
		byte[] registrationInfo = register.getBytes();
		senderToRegistry.sendData(registrationInfo, 1);
		this.receivers = new ArrayList<>();
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
			this.receivingPort = serverSocket.getLocalPort();
			System.out.println("Messaging node listening on port " + receivingPort);
			while(true)
			{
				Socket socket = serverSocket.accept();
				senders.add(new TCPSender(socket));
				//receivers.add(new TCPReceiver(socket));
			}
		} catch (IOException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws NumberFormatException, UnknownHostException, IOException
	{
		if(args.length == 2)
		{
			MessagingNode messagingNode = new MessagingNode(args[0], Integer.parseInt(args[1]));
			messagingNode.listenForConnections();
		}
		else
		{
			System.out.println("Proper usage: java cs455.overlay.node.MessagingNode <registry-host> <registry-port>");
		}
	}

}
