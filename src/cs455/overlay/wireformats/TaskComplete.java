// CS455HW1 Assignment
// Author: Matthew Borden
// Date: Feb 8, 2017
// Class: CS160
// Email: mborden21@gmail.com

package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskComplete implements Event {

	private int type;
	private String ipAddressAndPort;
	
	public TaskComplete(String ipAndPort)
	{
		this.type = 12;
		this.ipAddressAndPort = ipAndPort;
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
		
		byte[] addressBytes = ipAddressAndPort.getBytes();
		int addressLength = addressBytes.length;
		
		outputStream.writeInt(getType());
		outputStream.writeInt(addressLength);
		outputStream.write(addressBytes);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}

}
