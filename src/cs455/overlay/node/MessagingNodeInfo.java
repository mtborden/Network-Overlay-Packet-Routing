package cs455.overlay.node;

public class MessagingNodeInfo {

	public String ipAddress;
	public int portNumber;
	
	public MessagingNodeInfo(String ipAddress, int portNumber)
	{
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof MessagingNodeInfo)
		{
			MessagingNodeInfo otherNode = (MessagingNodeInfo)o;
			if(otherNode.ipAddress.equals(this.ipAddress) && otherNode.portNumber == this.portNumber)
			{
				return true;
			}
		}
		return false;
	}
	
	public String toString()
	{
		return "Address: " + ipAddress + " Port: " + portNumber;
	}
}
