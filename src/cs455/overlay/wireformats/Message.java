package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message implements Event {

	private int type;
	private String message;
	
	public Message(String message)
	{
		//System.out.println("MESSAGE CREATED");
		this.type = 10;
		this.message = message;
	}
	
	@Override
	public int getType() {
		return this.type;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
		
		byte[] messageBytes = message.getBytes();
		int messageLength = messageBytes.length;
		
		outputStream.writeInt(getType());
		outputStream.writeInt(messageLength);
		outputStream.write(messageBytes);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}

}
