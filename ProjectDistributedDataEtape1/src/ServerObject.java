import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ServerObject implements Serializable {
	
	private static final long serialVersionUID = 7513820961864278491L;
	// Object's server ID
    private int id;
    // The current lock state of the object
    private Lock state;
    // Reference to the shared object
    Object obj;
    // Client that is currently writing
    private Client_itf writer;
    // Clients that got lock_read state from the Object
	private Set<Client_itf> readers;
    
    public ServerObject(int id, Object o){
        this.id = id;
        this.state = Lock.NL;
        this.obj = o;
        this.writer = null;
        this.readers = new HashSet<Client_itf>();
    }

	// invoked by the user program on the client node
	public synchronized Object lock_read(Client_itf client) {
		if (this.state == Lock.WL) {
			
			// It must lock reduce for all write_lock clients
			try {
				System.out.println("ServerObject (WL - lock) LOCK READ ID - CLIENT " + this.id + " - " + client);
				obj = writer.reduce_lock(this.id); // Blocking call for the client
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.readers.add(this.writer); // The previous writer becomes a reader	
		}
		this.writer = null; // There is no more writer
		this.readers.add(client); // The client becomes a reader
		this.state = Lock.RL;
		
		return obj;
	}

	// invoked by the user program on the client node
	public synchronized Object lock_write(Client_itf client) {
		
		// Client demanding lock_write, the current writer should be invalidated updated to the Client
		if (this.writer != null) {
			
			try {
				this.obj = writer.invalidate_writer(this.id); // Blocking call
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			
			this.readers.remove(client);
			
			// Client demanding lock_write, all readers should be invalidated and set cleared
			for (Client_itf c : this.readers) {
				try {
					c.invalidate_reader(this.id); // Blocking call for the client
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// There's no longer readers
		this.readers.clear();
		// The current client becomes the writer
		this.writer = client;
		// Changes the lock to WL
		this.state = Lock.WL;
		
		return obj;
	}

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lock getState() {
        return state;
    }

    public void setState(Lock state) {
        this.state = state;
    }
}
