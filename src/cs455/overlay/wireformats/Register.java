package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Register implements Event {
	
	private int type = 1;
	private String ipAddress;
	private int portNumber;
	
	public Register(String ipAddress, int portNumber)
	{
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}
	
	@Override
	public int getType() {
		// FIXME Auto-generated method stub
		return this.type;
	}

	@Override
	public byte[] getBytes() throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
		
		//outputStream.writeInt(1);
		
		byte[] addressBytes = ipAddress.getBytes();
		int addressLength = addressBytes.length;
		outputStream.writeInt(addressLength);
		outputStream.write(addressBytes);
		outputStream.writeInt(portNumber);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		String bytesString = new String(marshalledBytes);
		System.out.println("Marshalled bytes: " + bytesString);
		return marshalledBytes;
	}

}
