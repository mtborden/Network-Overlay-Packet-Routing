package cs455.overlay.node;

public class MessagingNodeInfo {

	public String ipAddress;
	public int portNumber;	
	public int serverSocketPortNumber;
	
	public MessagingNodeInfo(String ipAddress, int portNumber, int serverSocketPortNumber)
	{
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		this.serverSocketPortNumber = serverSocketPortNumber;
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
		return ipAddress + ":" + portNumber;
	}
}
