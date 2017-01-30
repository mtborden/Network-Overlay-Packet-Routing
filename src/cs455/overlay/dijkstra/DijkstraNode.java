package cs455.overlay.dijkstra;

import java.util.ArrayList;

public class DijkstraNode {

	public int distanceFromSource;
	public String name;
	public ArrayList<Connection> connections;
	
	public DijkstraNode(String name)
	{
		this.name = name;
		this.distanceFromSource = Integer.MAX_VALUE;
		this.connections = new ArrayList<>();
	}
}
