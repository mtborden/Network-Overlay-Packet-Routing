// CS455HW1 Assignment
// Author: Matthew Borden
// Date: Feb 8, 2017
// Class: CS160
// Email: mborden21@gmail.com

package cs455.overlay.transport;

public class NodeSummation {

	public int numberSent;
	public int numberReceived;
	public int numberForwarded;
	public long summationReceived;
	public long summationSent;
	
	public NodeSummation(int numSent, int numReceived, int numForwarded, long sumReceived, long sumSent)
	{
		this.numberSent = numSent;
		this.numberReceived = numReceived;
		this.numberForwarded = numForwarded;
		this.summationReceived = sumReceived;
		this.summationSent = sumSent;
	}
}
