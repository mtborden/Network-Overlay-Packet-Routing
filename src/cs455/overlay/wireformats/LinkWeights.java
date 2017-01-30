package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cs455.overlay.node.Registry;

public class LinkWeights implements Event {

	private int type;
	private Registry registry;
	
	public LinkWeights(int type, Registry registry)
	{
		this.type = type;
		this.registry = registry;
	}
	
	@Override
	public int getType() {
		return this.type;
	}

	@Override
	public byte[] getBytes() throws IOException {

		int numberOfLinks = registry.overlayConnections.size();
		String linksString = registry.getLinks();
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
		
		byte[] linksBytes = linksString.getBytes();
		int linksLength = linksBytes.length;		
		outputStream.writeInt(getType());
		outputStream.writeInt(numberOfLinks);
		outputStream.writeInt(linksLength);
		outputStream.write(linksBytes);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}

}
