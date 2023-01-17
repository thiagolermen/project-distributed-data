import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static final long serialVersionUID = 5838449924074267286L;
	// Map of ID -> SharedObjects in cache
	private static HashMap<Integer, SharedObject> cachedObjects;
	// Reference to the server
	private static Server_itf server;
	// The instance of the client we send to the server
	private static Client_itf client;

	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init(){
		Client.cachedObjects = new HashMap<Integer, SharedObject>();
		int port = 4000; 
		String URL;
		try {
			URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/server";
			System.out.println("Server found");
			// Get the stub of the server object from the rmiregistry
			Client.server = (Server_itf) Naming.lookup(URL);
			// Initialize the Client
			Client.client = new Client();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Server not found");
			System.exit(-1);
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		SharedObject so = null;
		try {
			int id = server.lookup(name);
			if(id != -1) {
				Object obj = lock_read(id);
				so = new SharedObject(id, obj);
				so.unlock();;
				cachedObjects.put(id, so);
			} else {
				System.err.println("No Object with ID : " + id  + " found in cache");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return so;
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			int id = ((SharedObject) so).getId();
			server.register(name, id);
			// we uncomment the line below if we can have shared objects without its names
			// cachedObjects.put(id, (SharedObject) so);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		SharedObject so = null;
		try {
			int id = server.create(o);
			so = new SharedObject(id, o);
			cachedObjects.put(so.getId(), so);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return so;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		Object obj =  null;
		try {
			obj = server.lock_read(id, client);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {	
		Object obj =  null;
		try {
			obj = server.lock_write(id, client);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;	
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		return cachedObjects.get(id).reduce_lock();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		cachedObjects.get(id).invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		return cachedObjects.get(id).invalidate_writer();
	}
	
	
}
