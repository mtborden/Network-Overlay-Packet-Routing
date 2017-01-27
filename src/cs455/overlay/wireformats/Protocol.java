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
	}
	
	private enum Type
	{
		REGISTER_REQUEST,
		REGISTER_RESPONSE,
		DEREGISTER_REQUEST,
		DEREGISTRATION_RESPONSE,
		MESSAGING_NODES_LIST
	}
}
