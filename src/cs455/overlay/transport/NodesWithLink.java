package cs455.overlay.transport;

import java.net.Socket;

import cs455.overlay.node.MessagingNodeInfo;

public class NodesWithLink {
	
	private MessagingNodeInfo firstNode;
	private MessagingNodeInfo secondNode;
	private int linkWeight;
	
	public NodesWithLink(MessagingNodeInfo node1, MessagingNodeInfo node2, int weight)
	{
		this.firstNode = node1;
		this.secondNode = node2;
		this.linkWeight = weight;
	}
	
	public String toString()
	{
		return this.firstNode.ipAddress + ":" + this.firstNode.portNumber + " " + this.secondNode.ipAddress + ":" + this.secondNode.portNumber + " " + this.linkWeight;
	}
	
	public void setWeight(int weight)
	{
		this.linkWeight = weight;
	}
	
	public MessagingNodeInfo getFirstNode()
	{
		return this.firstNode;
	}
	
	public MessagingNodeInfo getSecondNode()
	{
		return this.secondNode;
	}
	
	public int getWeight()
	{
		return this.linkWeight;
	}
}
