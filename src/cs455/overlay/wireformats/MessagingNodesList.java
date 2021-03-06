package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cs455.overlay.node.MessagingNodeInfo;
import cs455.overlay.node.Registry;

public class MessagingNodesList implements Event{

	private Registry registry;
	private int type;
	private MessagingNodeInfo info;
	
	public MessagingNodesList(Registry registry, int type, MessagingNodeInfo info) {
		this.registry = registry;
		this.type = type;
		this.info = info;
	}
	
	@Override
	public int getType() {
		return this.type;
	}

	@Override
	public byte[] getBytes() throws IOException {
		String nodesString = registry.getMessagingNodes(info);
		int numberOfPeerNodes = Integer.parseInt(""+nodesString.charAt(0));
		nodesString = nodesString.substring(1);
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
		
		byte[] nodesBytes = nodesString.getBytes();
		int nodesLength = nodesBytes.length;		
		outputStream.writeInt(getType());
		outputStream.writeInt(numberOfPeerNodes);
		outputStream.writeInt(nodesLength);
		outputStream.write(nodesBytes);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}

	
}
