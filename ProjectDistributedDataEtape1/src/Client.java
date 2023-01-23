import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	// Map of ID -> SharedObjects corresponding to the objects with taken or cached lock (for a given client)
	private static HashMap<Integer, SharedObject> possessedObjects;
	// Reference to the server
	private static Server_itf server;
	// The instance of the client we send to the server (especially in ordre to make possible eventual callbacks)
	private static Client_itf client;
	// The instance is only used to have a reference on the client
	public Client() throws RemoteException {
		super();
	}

///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	/**
	 * Initialization of the client layer
	 */
	public static void init() {
		Client.possessedObjects = new HashMap<Integer, SharedObject>();
		// The same port as in the Server class
		int port = 4000; 
		try {
			String URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/server";
			System.out.println("Server found");
			// Get the stub of the server object from the rmiregistry
			Client.server = (Server_itf) Naming.lookup(URL);
			// Initialize the Client
			Client.client = new Client();
		} catch (Exception e) {
			System.err.println("Error during client layer initialization");
			e.printStackTrace();
		}
	}
	
	/**
	 * Lookup in the server registry
	 * @param name the name of the object that will be searched in the server registry
	 * @return the object that was found in the server registry with the given name, or null if it wasn't found
	 */
	public static SharedObject lookup(String name) {
		SharedObject so = null;
		try {
			int id = server.lookup(name);
			// If the given name was found in the server of objects
			if (id != -1) {
				// A little trick in order to obtain the reference on the object of interest
				Object obj = lock_read(id);
				so = new SharedObject(id, obj);
				so.unlock();
				
				// Adds the object to the map of possesed objects of the client (the create method and more particularly its corresponding instruction will logically not be executed)
				possessedObjects.put(id, so);
				
			} else {
				System.err.println("No Object with ID : " + id  + " found in cache");
			}
		} catch (Exception e) {
			System.err.println("Error during server registry consultation");
			e.printStackTrace();
		}
		return so;
	}		
	
	/**
	 * Binding in the server registry
	 * @param name the name of the object that will be registered in the server registry
	 * @param so the object that has been registered
	 */
	public static void register(String name, SharedObject_itf so) {
		try {
			// The unique (by construction) id of the shared object
			int id = ((SharedObject) so).getId();
			server.register(name, id);
		} catch (Exception e) {
			System.err.println("Error during attempt to register an object in the server registry");
			e.printStackTrace();
		}
	}
	
	/**
	 * Creation of a shared object
	 * @param obj the real object to be referenced by the created shared object
	 * @return the newly created shared object
	 */
	public static SharedObject create(Object obj) {
		SharedObject so = null;
		try {
			int id = server.create(obj);
			so = new SharedObject(id, obj);
			// Because the corresponding instruction in the lookup method has logically not been executed
			possessedObjects.put(so.getId(), so);
		} catch (Exception e) {
			System.err.println("Error during shared object creation");
			e.printStackTrace();
		}
		return so;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
/////////////////////////////////////////////////////////////

	/**
	 * Request a read lock to the server
	 * @param id the id of the object to lock in reading
	 * @return the reference on the object to take lock in reading on (useful for the lock_read method in the SharedObject class and for the trick in the local lookup method)
	 */
	public static Object lock_read(int id) {
		Object obj =  null;
		try {
			obj = server.lock_read(id, client);
		} catch (Exception e) {
			System.err.println("Error during read locking");
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Request a write lock to the server
	 * @param id the id of the object to lock in writing
	 * @return the reference on the object to take lock in writing on (useful for the write_lock method in the SharedObject class)
	 */
	public static Object lock_write (int id) {	
		Object obj =  null;
		try {
			obj = server.lock_write(id, client);
		} catch (Exception e) {
			System.err.println("Error during write locking");
			e.printStackTrace();
		}
		return obj;	
	}

	/**
	 * Receive a lock reduction request from the server
	 * @param id the id of the object which lock is to be reduced
	 * @return the reference on the object which look has been reduced
	 */
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		return possessedObjects.get(id).reduce_lock();
	}

	/**
	 * Receive a reader invalidation request from the server
	 * @param id the id of the object which lock in reading is to be invalidated
	 * @return the reference on the object which look in reading has been invalidated
	 */
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		possessedObjects.get(id).invalidate_reader();
	}

	/**
	* Receive a writer invalidation request from the server
	* @param id the id of the object which lock in writing is to be invalidated
	* @return the reference on the object which look in writing has been invalidated
	*/
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		return possessedObjects.get(id).invalidate_writer();
	}
}
