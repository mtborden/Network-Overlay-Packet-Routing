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
	
	public DijkstraNode(String name, int portNum)
	{
		this.name = name;
		this.distanceFromSource = Integer.MAX_VALUE;
		this.connections = new ArrayList<>();
		this.portNumber = portNum;
	}
	
	public void relaxEdges()
	{
		
	}
	
	Comparator<DijkstraNode> sortByDistance = new Comparator<DijkstraNode>() {

		@Override
		public int compare(DijkstraNode o1, DijkstraNode o2) {
			return (int) (o1.distanceFromSource - o2.distanceFromSource);
		}		
	};
	
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
}
