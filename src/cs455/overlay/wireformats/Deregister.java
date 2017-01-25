package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Deregister implements Event{
	
	private int type = 1;
	private String ipAddress;
	private int portNumber;
	
	public Deregister(String ipAddress, int portNumber)
	{
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
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
		
		byte[] addressBytes = ipAddress.getBytes();
		int addressLength = addressBytes.length;
		outputStream.writeInt(3);
		outputStream.writeInt(addressLength);
		outputStream.write(addressBytes);
		outputStream.writeInt(portNumber);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}

}
