import java.io.*;

public class ServerObject implements Serializable, SharedObject_itf {
	
    private Integer id;
    private Lock state;
    
    public ServerObject(Integer id){
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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Lock getState() {
        return state;
    }

    public void setState(Lock state) {
        this.state = state;
    }
}
