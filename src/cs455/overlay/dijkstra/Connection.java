package cs455.overlay.dijkstra;

public class Connection {
	
	public String firstNode;
	public String secondNode;
	public int weight;
	
	/**
	 * Constructor - address is name + : + portNumber (ex. 129.168.1.122:6100)
	 * This is used for computing shortest paths using Dijkstra's algorithm
	 * @param addressAndPort - address and port number of the connected node
	 * @param wt - weight of the connection
	 */
	public Connection(String firstNode, String secondNode, int wt)
	{
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.weight = wt;
	}
	
	public String toString()
	{
		return this.firstNode + "#" + this.secondNode + "#" + this.weight;
	}
}
