import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;
import java.rmi.registry.*;
import java.net.*;
import java.net.UnknownHostException;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static Map<Integer, SharedObject> cachedObjects;
	private static Server_itf server;

	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init(){
		cachedObjects = new Hashtable<>();
		int port = 4000; 
		String URL;
		try {
			URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/server";
			// get the stub of the server object from the rmiregistry
			server = new Server();
			server = (Server_itf) Naming.lookup(URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		try {
			int id = server.lookup(name);
			return cachedObjects.get(id);
			// TODO: if exists in server but doest exist in cache, put object in cache 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			int id = ((SharedObject) so).getId();
			server.register(name, id);
			cachedObjects.put(id, (SharedObject) so);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		try {
			int id = server.create(o);
			return new SharedObject(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
	}
}
