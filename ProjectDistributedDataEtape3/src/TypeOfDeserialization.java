
public class TypeOfDeserialization {
	
	
	static enum TypeDes {SERVER_CLIENT , CLIENT_SERVER}; // The types of deserialization (From server to client or from client to server)
	public static TypeDes typeDeserialization = TypeDes.SERVER_CLIENT; // The current type that will be used for deserialization
	
	/**
	 * @return type of deserialization
	 */
	public static TypeDes getTypeOfDeserialization() {
		return typeDeserialization;
	}
	
	/**
	 * @param toClientServer
	 */
	public static void setTypeOfDeserialization(boolean toClientServer) {
		if (toClientServer) {
			typeDeserialization = TypeDes.CLIENT_SERVER;
		} else {
			typeDeserialization = TypeDes.SERVER_CLIENT;
		}
	}
	
}
