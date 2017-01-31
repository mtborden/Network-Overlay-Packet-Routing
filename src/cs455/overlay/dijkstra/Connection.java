package cs455.overlay.dijkstra;

public class Connection {
	
	String connectedNodeWithPort;
	public int weight;
	
	/**
	 * Constructor - address is name + : + portNumber (ex. 129.168.1.122:6100)
	 * This is used for computing shortest paths using Dijkstra's algorithm
	 * @param addressAndPort - address and port number of the connected node
	 * @param wt - weight of the connection
	 */
	public Connection(String addressAndPort, int wt)
	{
		this.connectedNodeWithPort = addressAndPort;
		this.weight = wt;
	}
}
