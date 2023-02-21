import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ServerObject implements Serializable {
	
	// Object's server ID
    private int id;
    // The current lock state of the server object
    private Lock state;
    // Reference to the real object
    Object obj;
    // Client that has the write_lock on the object (at most one such client at a time)
    private Client_itf writer;
    // Clients that have the read_lock on the object
	private Set<Client_itf> readers;
	// Clients that have subscribed the object
	private Set<Callback_itf> subscribers;
    
    public ServerObject(int id, Object obj){
        this.id = id;
		// By convention the objects are not locked at their creation
        this.state = Lock.NL; 
        this.obj = obj;
        this.writer = null;
        this.readers = new HashSet<Client_itf>();
		this.subscribers = new HashSet<Callback_itf>();
    }

	/**
	 * Locks in reading the object for the client in entry. Synchronization is necessary to ensure the consistency of the object's state in terms of its readers and writers.
	 * @param client the client who requested the read lock for the object
	 * @return The reference on the object that has been locked in reading
	 */
	public synchronized Object lock_read(Client_itf client) {
		// There can be no new readers of the object as long as there is a writer. The eventuel writer must therefore give up the lock.
		if (this.state == Lock.WL) {
			try {
				// Blocking call, because if the lock is in taken mode, one has to wait for the running writer to finish what he wants to do with the object
				obj = writer.reduce_lock(this.id); // Blocking call for the client
			} catch (Exception e) {
				//System.err.println("Error when invalidating a writer to have a read lock");
				//e.printStackTrace();
			}
			// This is precisely the nuance of reducing the lock for the former writer instead of simply removing it 
			this.readers.add(this.writer);	
			// If an object is in read mode, there can be no writer from the associated server object's point of view
			this.writer = null;
			// Ensures the consistency of the associated server object's state
			this.state = Lock.RL;
		}
		// The client that has requested the read lock on the object becomes its new reader
		this.readers.add(client);
		return obj;
	}

	/**
	 * Locks in writing the object for the client in entry. Synchronization is necessary to ensure the consistency of the object's state in terms of its readers and writers
	 * @param client the client that has requested the write lock for the object
	 * @return The reference on the object that has been locked in writing
	 */
	public synchronized Object lock_write(Client_itf client) {
		// There can only be one writer at a time for the object. The current writer must therefore give up the lock.
		if (this.writer != null) {
			try {
				this.obj = writer.invalidate_writer(this.id);
			} catch (Exception e) {
				//System.err.println("Error when invalidating a writer to have a write lock");
				//e.printStackTrace();
			}
		} else {
			// A client cannot be both reader and writer for an object from the associated server object's point of view
			this.readers.remove(client);
			// Client demanding lock_write, all readers should be invalidated and set cleared
			for (Client_itf c : this.readers) {
				try {
					// Blocking call, because the future writer must wait for the current readers to finish consulting the object
					c.invalidate_reader(this.id);
				} catch (Exception e) {
					//ystem.err.println("Error when invalidating readers to have a write lock");
					//e.printStackTrace();
				}
			}
		}
		// If an object is in write mode, there can be no readers from the associated server object's point of view
		this.readers.clear();
		// The client that has requested the write lock on the object becomes its new writer
		this.writer = client;
		// Ensures the consistency of the associated server object's state
		this.state = Lock.WL;
		return obj;
	}

	/** Subscribe a client to the object adding a callback referencing the current object to the client
	 * @param client
	 **/
	public void subscribe(Callback_itf cb, int objectHasChanged){
		try {
			this.subscribers.add(cb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unsubscribe(Callback_itf cb){
		try {
			this.subscribers.remove(cb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Notify all the clients that has subscribed that the current object has been changed
	 **/
	public void notifySubscribers(){
		for (Callback_itf cb : subscribers) {
			try {
				cb.notifySubscriber(this.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    // Getters and setters

    public int getId() {
        return id;
    }
	
	public Lock getState() {
		return state;
	}

    public void setId(int id) {
        this.id = id;
    }

    public void setState(Lock state) {
        this.state = state;
    }
}
