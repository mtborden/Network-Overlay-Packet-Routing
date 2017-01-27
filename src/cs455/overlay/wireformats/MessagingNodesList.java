package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cs455.overlay.node.Registry;

public class MessagingNodesList implements Event{

	private Registry registry;
	private int type;
	private Socket socket;
	
	public MessagingNodesList(Registry registry, int type, Socket socket) {
		this.registry = registry;
		this.type = type;
		this.socket = socket;
	}
	
	@Override
	public int getType() {
		return this.type;
	}

	@Override
	public byte[] getBytes() throws IOException {
		String nodesString = registry.getMessagingNodes(socket);
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
