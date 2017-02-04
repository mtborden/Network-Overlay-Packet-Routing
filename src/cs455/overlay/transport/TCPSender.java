package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cs455.overlay.node.MessagingNode;

public class TCPSender {
	
	private Socket socket;
	private DataOutputStream outputStream;
	private MessagingNode node;
	
	public TCPSender(Socket socket) throws IOException
	{
		this.socket = socket;
		this.outputStream = new DataOutputStream(socket.getOutputStream());
	}
	
	public TCPSender(Socket socket, MessagingNode node) throws IOException
	{
		this.socket = socket;
		this.node = node;
		this.outputStream = new DataOutputStream(socket.getOutputStream());
	}
	
	public void sendData(byte[] dataToSend) throws IOException
	{
		int dataLength = dataToSend.length;
		outputStream.writeInt(dataLength);
		outputStream.write(dataToSend, 0, dataLength);
		outputStream.flush();
		//System.out.println("SENDING\n");
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
}
