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
        this.state = Lock.F;
        this.obj = o;
        this.writer = null;
        this.readers = new HashSet<Client_itf>();
    }

	// invoked by the user program on the client node
	public synchronized Object lock_read(Client_itf client) {
		
		// The current Client demands reading
		this.readers.add(client);
		
		if (this.state == Lock.WL) {
			
			// It must lock reduce for all write_lock clients
			try {
				obj = writer.reduce_lock(this.id); // The writer will stop writing
				this.readers.add(this.writer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.writer = null;
			this.state = Lock.RL;
			
		}
		return obj;
	}

	// invoked by the user program on the client node
	public synchronized Object lock_write(Client_itf client) {
		
		// Client demanding lock_write, all readers should be invalidated and set cleared
		for (Client_itf c : this.readers) {
			try {
				c.invalidate_reader(this.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.readers.clear();
		
		// Client demanding lock_write, the current writer should be invalidated updated to the Client
		if (this.writer != null) {
			
			try {
				
				this.obj = writer.invalidate_writer(this.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.state = Lock.WL;
		this.writer = client;
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
