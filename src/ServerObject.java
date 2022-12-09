import java.io.*;

public class ServerObject implements Serializable, SharedObject_itf {
	
	// Object's server ID
    private int id;
    // The current lock state of the object
    private Lock state;
    
    public ServerObject(int id, Object o){
        this.id = id;
        this.state = Lock.NL;
    }

	// invoked by the user program on the client node
	public void lock_read() {
	}

	// invoked by the user program on the client node
	public void lock_write() {
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
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
