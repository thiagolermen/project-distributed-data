import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private Integer id;
	public Object obj;
	public Lock state;

	public SharedObject(int id){
        this.id = id;
		this.state = Lock.NL;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		if (state == Lock.NL) {
			this.state = (Lock) Client.lock_read(this.getId());
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
	}

	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
	}

	public synchronized Object invalidate_writer() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
