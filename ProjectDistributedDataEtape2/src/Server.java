import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf {
	
	// Table of all server objects (not necessarily named)
	private HashMap<Integer, ServerObject> serverObjects;
	// Server registry of named server objects
	private HashMap<String, ServerObject> serverRegistry;
	// ID counter used to ensure the uniqueness of the id of real objects
	private int currId;

	protected Server() throws RemoteException {
		super();
		this.currId = 0; // Just a convention
		this.serverObjects = new HashMap<Integer, ServerObject>();
		this.serverRegistry = new HashMap<String, ServerObject>();
	}

	/**
	 * Lookup in the server registry
	 * @param name the name of the object that will be searched in the server registry
	 * @return the id of the object that was found in the server registry with the given name, or -1 if it has not been found
	 */
	public int lookup(String name) throws RemoteException {
		// Default value used by the corresponding method of the Client class
		int id = -1;
		ServerObject foundObject = null;
		try {
			synchronized (serverRegistry) {
				foundObject = serverRegistry.get(name);
			}
			if (foundObject != null) {
				id = foundObject.getId();
			}
		} catch (Exception e) {
			System.err.println("Error during name server consultation");
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Binding in the server registry
	 * @param name the name of the object that will be registered in the server registry
	 * @return the id of the object that has been registered
	 */
	public void register(String name, int id) throws RemoteException {
		ServerObject foundObject = null;
		try {
			synchronized (serverObjects) {
				foundObject = serverObjects.get(id);
			}
			if (foundObject != null) {
				// Precautions in order to always keep the server registry in a coherent state 
				synchronized (serverRegistry) {
					serverRegistry.put(name, foundObject);
				}
			} else {
				System.err.println("No ServerObject with ID : " + id  + " found");
			}
		} catch (Exception e) {
			System.err.println("Error during attempt to register an object in the server registry");
			e.printStackTrace();
		}
	}

	/**
	 * Creation of an object
	 * @param obj the name of the object that will created (i.e. will be added to the service objects table here)
	 * @return the id of the object that has been created
	 */
	public int create(Object obj) throws RemoteException {
		// Just to avoid a compilation error on the return instruction, and will always be reinitialized by contruction of the method
		int id = -1;
		try {
			// Precautions in order to always keep the server registry in a coherent state 
			synchronized (serverObjects) {
				id = this.currId;
				this.currId++;
				ServerObject so = new ServerObject(id, obj);
				serverObjects.put(id, so);
			}
		} catch (Exception e) {
			System.err.println("Error during object creation");
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Request a read lock to the associated service object
	 * @param id the id of the object to lock in reading
	 * @return the reference on the object to take lock in reading on
	 */
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		// Just to avoid a compilation error on the return instruction, and will always be reinitialized by contruction of the method
		Object obj = null;
		ServerObject so = null;
		try {
			synchronized (serverObjects) {
				so  = this.serverObjects.get(id);
			}
			if (so != null) {
				obj = so.lock_read(client);
			} else {
				System.err.println("No ServerObject with ID : " + id  + " found");
			}
		}catch (Exception e){
			System.err.println("Error during read locking");
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Request a write lock to the associated service object
	 * @param id the id of the object to lock in writing
	 * @return the reference on the object to take lock in writing on
	 */
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		// Just to avoid a compilation error on the return instruction, and will always be reinitialized by contruction of the method
		Object obj = null;
		ServerObject so = null;
		try {
			synchronized (serverObjects) {
				so  = this.serverObjects.get(id);
			}
			if (so != null) {
				obj = so.lock_write(client);
			} else {
				System.err.println("No ServerObject with ID : " + id  + " found");
			}
			
		} catch (Exception e){
			System.err.println("Error during write locking");
			e.printStackTrace();
		}
		return obj;
	}
	
	public static void main(String args[]) {
		// The same port as in the Client class
		int port = 4000; 
		try {
			 // Launching the naming service – rmiregistry – within the JVM (the same as the only server's one here actually)
			 LocateRegistry.createRegistry(port);
			 // Create an instance of the server object
			 Server server = new Server();
			 String URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/server";
			// Associate the computed URL with the server
			 Naming.rebind(URL, server);
			 System.out.println("Server '"+ URL +"' bound in registry");
		} catch (Exception e) {
			System.err.println("Error during server initialization");
			e.printStackTrace();
		}
	}

}
