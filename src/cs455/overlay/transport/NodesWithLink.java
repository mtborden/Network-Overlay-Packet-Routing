package cs455.overlay.transport;

import java.net.Socket;

import cs455.overlay.node.MessagingNode;

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
		return this.firstNode.getInetAddress().toString() + " " + this.firstNode.getPort() + " " + this.secondNode.getInetAddress().toString() + " " + this.secondNode.getPort() + " " + this.linkWeight;
	}
}
