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

public class TaskSummaryResponse implements Event {

	private int type;
	private int numberSent;
	private int numberReceived;
	private int numberForwarded;
	private int summationSent;
	private int summationReceived;
	
	public TaskSummaryResponse(int numSent, int numReceived, int numForwarded, int sumSent, int sumReceived) {
		this.numberSent = numSent;
		this.numberReceived = numReceived;
		this.numberForwarded = numForwarded;
		this.summationSent = sumSent;
		this.summationReceived = sumReceived;
		this.type = 14;
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
		
		String numberSentString = "" + numberSent;
		String numberReceivedString = "" + numberReceived;
		String numberForwardedString = "" + numberForwarded;
		String summationSentString = "" + summationSent;
		String summationReceivedString = "" + summationReceived;
		
		byte[] numberSentBytes = numberSentString.getBytes();
		int numberSentLength = numberSentBytes.length;
		byte[] numberReceivedBytes = numberReceivedString.getBytes();
		int numberReceivedLength = numberReceivedBytes.length;
		byte[] numberForwardedBytes = numberForwardedString.getBytes();
		int numberForwardedLength = numberForwardedBytes.length;
		byte[] summationSentBytes = summationSentString.getBytes();
		int summationSentLength = summationSentBytes.length;
		byte[] summationReceivedBytes = summationReceivedString.getBytes();
		int summationReceivedLength = summationReceivedBytes.length;
		
		outputStream.writeInt(getType());
		outputStream.writeInt(numberSentLength);
		outputStream.write(numberSentBytes);
		outputStream.writeInt(numberReceivedLength);
		outputStream.write(numberReceivedBytes);
		outputStream.writeInt(numberForwardedLength);
		outputStream.write(numberForwardedBytes);
		outputStream.writeInt(summationSentLength);
		outputStream.write(summationSentBytes);
		outputStream.writeInt(summationReceivedLength);
		outputStream.write(summationReceivedBytes);
		
		/*byte[] addressBytes = ipAddress.getBytes();
		int addressLength = addressBytes.length;
		outputStream.writeInt(1);
		outputStream.writeInt(addressLength);
		outputStream.write(addressBytes);
		outputStream.writeInt(portNumber);
		outputStream.writeInt(listeningPort);*/
		
		outputStream.flush();
		marshalledBytes = byteArrayOutputStream.toByteArray();
		
		byteArrayOutputStream.close();
		outputStream.close();
		
		return marshalledBytes;
	}

}