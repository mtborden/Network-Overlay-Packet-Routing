package cs455.overlay.dijkstra;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;

public class DijkstraSocket {

	public int distanceFromSource;
	public String name;
	public ArrayList<Connection> connections;
	public int portNumber;
	
	public DijkstraSocket(String name, int portNum)
	{
		this.name = name;
		this.distanceFromSource = Integer.MAX_VALUE;
		this.connections = new ArrayList<>();
		this.portNumber = portNum;
	}
	
	public void setUpInfo(String name, int portNum)
	{
		this.name = name;
		this.distanceFromSource = Integer.MAX_VALUE;
		this.connections = new ArrayList<>();
		this.portNumber = portNum;
	}
	
	public void addConnection(String addressAndPort, int linkWeight)
	{
		this.connections.add(new Connection(addressAndPort, linkWeight));
	}
	
	Comparator<DijkstraSocket> sortByDistance = new Comparator<DijkstraSocket>() {

		@Override
		public int compare(DijkstraSocket o1, DijkstraSocket o2) {
			return (int) (o1.distanceFromSource - o2.distanceFromSource);
		}
		
	};
}
