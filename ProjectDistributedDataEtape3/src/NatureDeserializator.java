
public class NatureDeserializator {
	
	
	static enum NatDes {CLIENT , SERVER}; // The nature of deserializator (Server or client)
	public static NatDes natureDeserializator; // The default value of the nature of the deserializator
	
	/**
	 * Retrieves the nature of deserializator (client or server)
	 * @return nature of the deserializator
	 */
	public static NatDes getNatureDeserializator() {
		return natureDeserializator;
	}
	
	/**
	 * Sets the value of the nature of deserializator
	 * @param toClientServer boolean to decide the from whom the call of deserializator comes (client or server)
	 */
	public static void setNatureDeserializator(NatDes nature) {
			natureDeserializator = nature;
	}
	
}
