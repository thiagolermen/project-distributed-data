import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Server extends UnicastRemoteObject implements Server_itf {
	
	private static final long serialVersionUID = -5442950059578179859L;
	// Registry of ServerObjects
	private Map<Integer, ServerObject> serverObjects;
	// Map of ServerObjects
	private Map<String, ServerObject> registry;
	// Current ID to be given to a Object
	private int currId;

	protected Server() throws RemoteException {
		super();
		this.currId = 0;
		this.serverObjects = new HashMap<Integer,ServerObject>();
		this.registry = new HashMap<String,ServerObject>();
	}

	public int lookup(String name) throws RemoteException {

		int id = -1;
		try {
			ServerObject foundObject = registry.get(name);
			if (foundObject != null){
				id = foundObject.getId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return id;
	}

	public void register(String name, int id) throws RemoteException {
		try {
			ServerObject foundObject = serverObjects.get(id);
			if (foundObject != null) {
				synchronized (this) {
					registry.put(name, foundObject);		
				}
			} else {
				System.out.println("No ServerObject with ID : " + id  + " found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int create(Object o) throws RemoteException {
		int id = -1;
		try {
			synchronized (this) {
				id = this.currId;
				this.currId++;
				ServerObject so = new ServerObject(id, o);
				serverObjects.put(id, so);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	public Object lock_read(int id, Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lock_write(int id, Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]) {
		 int port = 4000; 
		 String URL;
		 String name = "/server";
		 try {
			 // Launching the naming service – rmiregistry – within the JVM
			 Registry registry = LocateRegistry.createRegistry(port);
			 
			 // Create an instance of the server object
			 Server server = new Server();
			 
			 // compute the URL of the server
			 URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + name;
			 Naming.rebind(URL, server);
			 
			 System.out.println("Server '"+ name +"' bound in registry");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
