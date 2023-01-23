import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private Integer id; // The id of the shared object
	public boolean waiting; // Permits to know if a thread should wait or not (synchronization)
	public Object obj; // General reference to object of a given application
	public Lock state; // Current lock state of the shared object

	public SharedObject(int id, Object obj){
        this.id = id;
        this.waiting = false;
        this.obj = obj;
		this.state = Lock.NL;
	}
	
	/**
	 * Invoked by the user program on the client node once it demands for a read lock
	 */
	public void lock_read() {
		Lock aux_state = null;
		Object aux_obj = null;
		// Indicates if the request is remote (true) or local (false). So, the value below can be changed during execution
		boolean askToServer = false;
		// Synchronization is necessary to ensure the consistency of the state of the shared object
		synchronized (this) {
			// Waits while a client has the lock in taken mode
			while (this.waiting) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.err.println("Error while waiting for a lock to be released");
					e.printStackTrace();
				}
			}
			switch (this.state) {
			case NL:
				// There is no corresponding lock in the client's cache. The request must therefore go through the server
				askToServer = true;
				aux_state = Lock.RLT;
				break;
			case RLC:
				aux_state = Lock.RLT;
				break;
			case WLC:
				// A nuanced behavior, imposed by the project
				aux_state = Lock.RLT_WLC;
				break;
			default:
				break;	
			}
		}
		
		if (askToServer) {
			// Leads to a sequence of calls: client -> server -> server object
			aux_obj = Client.lock_read(this.id);
		}
		synchronized (this) {
			if (aux_obj != null) {	
				this.obj = aux_obj;
			} 
			if (aux_state != null) {	
				this.state = aux_state;
			}
		}
		
	}

	/**
	 * Invoked by the user program on the client node once it demands for a write lock
	 */
	public void lock_write() { 
		Lock aux_state = null;
		Object aux_obj = null;
		// Indicates if the request is remote (true) or local (false). So, the value below can be changed during execution
		boolean askToServer = false;
		// Synchronization is necessary to ensure the consistency of the state of the shared object
		synchronized (this) {
			// Waits while a client has the lock is taken
			while (this.waiting) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.err.println("Error while waiting for a lock to be released");
					e.printStackTrace();
				}
			}
			switch (this.state) {
			case NL:
				// There is no corresponding lock in the client's cache. The request must therefore go through the server
				askToServer = true;
				aux_state = Lock.WLT;
				break;
			case RLC:
				// The write lock has a higher status than the read lock. The request must therefore go through the server
				askToServer = true;
				aux_state = Lock.WLT;
				break;
			case WLC:
				aux_state = Lock.WLT;
				break;
			default:
				break;
			}
		}
		if (askToServer) {
			// Leads to a sequence of calls: client -> server -> server object
			aux_obj = Client.lock_write(this.id);
		}
		synchronized (this) {
			if (aux_obj != null) {	
				this.obj = aux_obj;
			} 
			if (aux_state != null) {	
				this.state = aux_state;
			}
		}
	}

	/**
	 * 	Invoked by the user program on the client node once it demands to put a lock in cache. Synchronization is necessary to ensure the consistancy of the state of the shared object
	 */
	public synchronized void unlock() {
		switch (this.state) {
		case RLT:
			this.state = Lock.RLC;
			break;
		case WLT:
			this.state = Lock.WLC;
			break;
		case RLT_WLC:
			this.state = Lock.WLC;
			break;
		default:
			break;
		}
		try {
			// Allows the potential client waiting for a lock to receive it
			this.notify();
		} catch (Exception e){
			System.err.println("Error during lock release notification");
			e.printStackTrace();
		}
	}

	/**
	 * Callback invoked remotely by the server. Synchronization is necessary to ensure the consistency of the state of the shared object
	 * It is a blocking call for clients that are demanding the lock
	 * @return the general reference to object of the application
	 */
	public synchronized Object reduce_lock() {
		// Essential for synchronization. Helps to ensure no interference between lock requests and lock reductions
		this.waiting = true;
		switch (this.state) {
		case WLT:
			// The eventual current writer, as long as he has the write lock in taken mode, has the right to finish what he started
			while (this.state == Lock.WLT) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.err.println("Error while waiting for a write lock to be released");
					e.printStackTrace();
				}
			}
		case WLC:
			this.state = Lock.RLC;
			break;
		case RLT_WLC:
			// A nuanced behavior, imposed by the project
			this.state = Lock.RLT;
			break;
		default:
			break;
		}
		// Unlocks a possible client who requested a read lock
		this.waiting = false;	
		try {
			this.notify();
		} catch (Exception e) {
			System.err.println("Error during notification of the end of lock reduction");
			e.printStackTrace();
		}
		return this.obj;
	}

	/**
	 * Callback invoked remotely by the server. Synchronization is necessary to ensure the consistency of the state of the shared object
	 */
	public synchronized void invalidate_reader() {
		// Essential for synchronization. Helps to ensure no interference between lock requests and lock invalidations
		this.waiting = true;
		switch (this.state) {
		case RLT:
			// The eventual current readers, as long as they have the read lock in taken mode, have the right to finish what they started
			while (this.state == Lock.RLT) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.err.println("Error while waiting for a read lock to be released");
					e.printStackTrace();
				}
			}
		case RLC:
			this.state = Lock.NL;
			break;	
		default:
			break;	
		}
		// Unlocks a possible client who requested a write lock
		this.waiting = false;
		try {
			this.notify();
		} catch (Exception e) {
			System.err.println("Error during notification of the end of lock invalidation");
			e.printStackTrace();
		}
	}

	/**
	 * ... Synchronization is necessary to ensure the consistancy of the state of the shared object
	 * @return
	 */
	public synchronized Object invalidate_writer() {
		// Essential for synchronization. Helps to ensure no interference between lock requests and lock invalidations
		this.waiting = true;
		switch (this.state) {
		case WLT:
			// The eventual current writer, as long as he has the write lock in taken mode, has the right to finish what he started
			while (this.state == Lock.WLT) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.err.println("Error while waiting for a write lock to be released");
					e.printStackTrace();
				}
			}
		case WLC:
			this.state = Lock.NL;
			break;
		case RLT_WLC:
			// The eventual current readers, as long as they have the read lock in taken mode, have the right to finish what they started
			while (this.state == Lock.RLT_WLC) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.err.println("Error while waiting for a read lock to be released");
					e.printStackTrace();
				}
			}
			this.state = Lock.NL;
			break;
		default:
			break;
		}
		// Unlocks a possible client who requested a write lock
		this.waiting = false;
		try {
			this.notify();
		} catch (Exception e) {
			System.err.println("Error during notification of the end of lock invalidation");
			e.printStackTrace();
		}
		return this.obj;
	}
	
	/**
	 * Provides eventual specific treatment during the deserialization from the client side
	 * @return shared object with id == this.id
	 * @throws ObjectStreamException
	 */
	protected Object readResolve() throws ObjectStreamException{
		// If the current call of deserialization is type Server to client, just return the current object
		if (NatureDeserializator.getNatureDeserializator() == NatureDeserializator.NatDes.SERVER) {
			return this;	
		} else {			
			// Check if object stub already exists, if not creates a new one and returns it
			SharedObject so = Client.getStub(this.id);
			if (so == null) {
				so = Client.generateStub(getId(), so);
			}
			return so;
		}
	}
	
	// Getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
