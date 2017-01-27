package cs455.overlay.transport;

import java.net.Socket;

public class NodesWithLink {
	
	private Socket firstNode;
	private Socket secondNode;
	private int linkWeight;
	
	public NodesWithLink(Socket node1, Socket node2, int weight)
	{
		this.firstNode = node1;
		this.secondNode = node2;
		this.linkWeight = weight;
	}
	
	public String toString()
	{
		return this.firstNode.getInetAddress().toString() + ":" + this.firstNode.getPort() + " " + this.secondNode.getInetAddress().toString() + ":" + this.secondNode.getPort() + " " + this.linkWeight;
	}
	
	public void setWeight(int weight)
	{
		this.linkWeight = weight;
	}
	
	public Socket getFirstNode()
	{
		return this.firstNode;
	}
	
	public Socket getSecondNode()
	{
		return this.secondNode;
	}
	
	public int getWeight()
	{
		return this.linkWeight;
	}
}
