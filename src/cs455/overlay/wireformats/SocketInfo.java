package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SocketInfo implements Event {
	
	private String socketInfo;
	private int type;
	
	public SocketInfo(String info) {
		this.socketInfo = info;
		this.type = 7;
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
		
		byte[] infoBytes = socketInfo.getBytes();
		int infoLength = infoBytes.length;
		outputStream.writeInt(getType());
		outputStream.writeInt(infoLength);
		outputStream.write(infoBytes);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}
	
}
