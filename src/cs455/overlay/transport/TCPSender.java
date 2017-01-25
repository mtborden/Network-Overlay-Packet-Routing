package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {
	
	private Socket socket;
	private DataOutputStream outputStream;
	
	public TCPSender(Socket socket) throws IOException
	{
		this.socket = socket;
		this.outputStream = new DataOutputStream(socket.getOutputStream());
	}
	
	public void sendData(byte[] dataToSend) throws IOException
	{
		int dataLength = dataToSend.length;
		outputStream.writeInt(dataLength);
		outputStream.write(dataToSend, 0, dataLength);
		outputStream.flush();
	}
}
