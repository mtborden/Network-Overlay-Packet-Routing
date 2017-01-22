package cs455.overlay.wireformats;

import java.util.HashMap;

public class Protocol {
	public HashMap<Integer, Type> types;
	
	public Protocol()
	{
		types = new HashMap<>();
		types.put(1, Type.REGISTER_REQUEST);
	}
	
	private enum Type
	{
		REGISTER_REQUEST
	}
}
