import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Server extends UnicastRemoteObject implements Server_itf {

	private Map<String, ServerObject> serverObjects;
	private static Integer currId = 0;

	protected Server() throws RemoteException {
		super();
		this.serverObjects = new HashMap<>();
	}

	@Override
	public int lookup(String name) throws RemoteException {

		try {
			ServerObject found_object = serverObjects.get(name);
			if (found_object != null){
				return found_object.getId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return -1;
	}

	@Override
	public void register(String name, int id) throws RemoteException {
		try {
			serverObjects.put(name, new ServerObject(id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int create(Object o) throws RemoteException {
		try {
			serverObjects.put("", o);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, ServerObject> getServerObjects() {
		return serverObjects;
	}

	public void setServerObjects(Map<String, ServerObject> serverObjects) {
		this.serverObjects = serverObjects;
	}
	
	public static void main(String args[]) {
		 int port = 4000; 
		 String URL;
		 try {
			 // Launching the naming service – rmiregistry – within the JVM
			 Registry registry = LocateRegistry.createRegistry(port);
			 
			 // Create an instance of the server object
			 Server_itf server = new Server();
			 
			 // compute the URL of the server
			 URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/server";
			 Naming.rebind(URL, server);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
