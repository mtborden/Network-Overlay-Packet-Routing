package cs455.overlay.wireformats;

import java.util.HashMap;

public class Protocol {
	public HashMap<Integer, Type> types;
	
	public Protocol()
	{
		types = new HashMap<>();
		types.put(1, Type.REGISTER_REQUEST);
		types.put(2, Type.REGISTRATION_RESPONSE);
		types.put(3, Type.DEREGISTER_REQUEST);
		types.put(4, Type.DEREGISTRATION_RESPONSE);
	}
	
	private enum Type
	{
		REGISTER_REQUEST,
		REGISTRATION_RESPONSE,
		DEREGISTER_REQUEST,
		DEREGISTRATION_RESPONSE
	}
}
