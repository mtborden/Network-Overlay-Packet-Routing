package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.wireformats.Protocol;

public class TCPReceiver implements Runnable {

	private Socket socket;
	private DataInputStream inputStream;
	private Protocol protocol;
	
	public TCPReceiver(Socket socket, Protocol protocol) throws IOException{
		this.socket = socket;
		this.inputStream = new DataInputStream(socket.getInputStream());
		this.protocol = protocol;
	}
	
	@Override
	public void run() {
		int dataLength;
		int infoType;
		
		while(socket != null)
		{
			try
			{
				infoType = inputStream.readInt();
				System.out.println(protocol.types.get(infoType));
				dataLength = inputStream.readInt();
				unpackBytes(infoType, dataLength);				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void unpackBytes(int type, int dataLength) throws IOException
	{
		switch(type)
		{
			case 1:
				byte[] data = new byte[dataLength];
				inputStream.readFully(data, 0, dataLength);
				String dataString = new String(data);
				System.out.println(dataString);
				break;
			default:
				break;
		}
	}

}
