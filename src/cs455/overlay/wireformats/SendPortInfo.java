package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendPortInfo implements Event {

	private int type;
	private int listeningPortNumber;
	private String address;
	
	public SendPortInfo(int portNumber, String addr) {
		this.type = 9;
		this.listeningPortNumber = portNumber;
		this.address = addr;
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
		
		String addressWithPort = this.address + ":" + this.listeningPortNumber;
		
		byte[] addressBytes = addressWithPort.getBytes();
		int addressLength = addressBytes.length;
		outputStream.writeInt(this.getType());
		outputStream.writeInt(addressLength);
		outputStream.write(addressBytes);
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}

}
