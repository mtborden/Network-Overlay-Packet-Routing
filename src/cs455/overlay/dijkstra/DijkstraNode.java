package cs455.overlay.dijkstra;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;

import cs455.overlay.node.MessagingNodeInfo;

public class DijkstraNode {

	public int distanceFromSource;
	public String name;
	public ArrayList<NodeWithDistance> connections;
	public int portNumber;
	private DijkstraNode parent;
	
	public DijkstraNode(String name, int portNum)
	{
		this.name = name;
		this.distanceFromSource = Integer.MAX_VALUE;
		this.connections = new ArrayList<>();
		this.portNumber = portNum;
		this.parent = null;
	}
	
	//copy constructor
	public DijkstraNode(DijkstraNode nodeToCopy)
	{
		this.name = nodeToCopy.name;
		this.distanceFromSource = nodeToCopy.distanceFromSource;
		this.portNumber = nodeToCopy.portNumber;
		this.connections = new ArrayList<>();
		for(int x = 0; x < nodeToCopy.connections.size(); x++)
		{
			NodeWithDistance connection = nodeToCopy.connections.get(x);
			this.connections.add(new NodeWithDistance(connection.nodeAddress, connection.nodePortNumber, connection.linkWeight));
		}
	}
	
	public String toString()
	{
		return this.name + ":" + this.portNumber;
	}
	
	public boolean equals(Object other)
	{
		if(other instanceof DijkstraNode)
		{
			DijkstraNode node = (DijkstraNode)other;
			if(name.equals(node.name) && portNumber == node.portNumber)
			{
				return true;
			}
		}
		return false;
	}

	public DijkstraNode getParent() {
		return parent;
	}

	public void setParent(DijkstraNode parent) {
		this.parent = parent;
	}
}
