package cs455.overlay.wireformats;

import java.util.HashMap;

public class Protocol {
	public HashMap<Integer, Type> types;
	
	public Protocol()
	{
		types = new HashMap<>();
		types.put(1, Type.REGISTER_REQUEST);
		types.put(2, Type.REGISTER_RESPONSE);
		types.put(3, Type.DEREGISTER_REQUEST);
		types.put(4, Type.DEREGISTRATION_RESPONSE);
		types.put(5, Type.MESSAGING_NODES_LIST);
		types.put(6, Type.LINK_WEIGHTS);
		types.put(7, Type.SOCKET_INFO);
		types.put(8, Type.RECEIVED_LINK_WEIGHTS);
		types.put(9, Type.SEND_PORT_INFO);
		types.put(10, Type.MESSAGE);
		types.put(11, Type.TASK_INITIATE);
		types.put(12, Type.TASK_COMPLETE);
		types.put(13, Type.PULL_TRAFFIC_SUMMARY);
		types.put(14, Type.TASK_SUMMARY_RESPONSE);
	}
	
	private enum Type
	{
		REGISTER_REQUEST,
		REGISTER_RESPONSE,
		DEREGISTER_REQUEST,
		DEREGISTRATION_RESPONSE,
		MESSAGING_NODES_LIST,
		LINK_WEIGHTS,
		SOCKET_INFO,
		RECEIVED_LINK_WEIGHTS,
		SEND_PORT_INFO,
		MESSAGE,
		TASK_INITIATE,
		TASK_COMPLETE,
		PULL_TRAFFIC_SUMMARY,
		TASK_SUMMARY_RESPONSE
	}
}
