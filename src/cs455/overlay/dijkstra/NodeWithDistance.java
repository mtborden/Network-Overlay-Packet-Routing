package cs455.overlay.dijkstra;

public class NodeWithDistance {

	public String nodeAddress;
	public int nodePortNumber;
	public int linkWeight;
	
	public NodeWithDistance(String nodeName, int portNum, int weight)
	{
		this.nodeAddress = nodeName;
		this.nodePortNumber = portNum;
		this.linkWeight = weight;
	}
}
