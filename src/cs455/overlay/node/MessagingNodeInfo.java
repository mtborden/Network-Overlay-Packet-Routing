package cs455.overlay.node;

public class MessagingNodeInfo {

	public String ipAddress;
	public int portNumber;	
	public int serverSocketPortNumber;
	
	/**
	 * This constructor is to be used only for deregistration
	 * @param ipAddress - ip address of the node wanting to deregister
	 * @param portNumber - port number of the node wanting to deregister
	 */
	public MessagingNodeInfo(String ipAddress, int portNumber)
	{
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}
	
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
	
	public boolean equalsUsingServerSocketPort(Object o)
	{
		if(o instanceof MessagingNodeInfo)
		{
			MessagingNodeInfo otherNode = (MessagingNodeInfo)o;
			if(otherNode.ipAddress.equals(this.ipAddress) && otherNode.serverSocketPortNumber == this.serverSocketPortNumber)
			{
				return true;
			}
		}
		return false;
	}
	
	public String toString()
	{
		return ipAddress + ":" + serverSocketPortNumber;
	}
}
