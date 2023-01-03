import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private static final long serialVersionUID = 899922328733830816L;
	private Integer id;
	public boolean waits;
	public Object obj;
	public Lock state;

	public SharedObject(int id, Object obj){
        this.id = id;
        this.waits = false;
        this.obj = obj;
		this.state = Lock.NL;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		
		while (this.waits) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		switch (this.state) {
		case NL:
			//demander le verrou 
			this.state = Lock.RLT;
			break;
		case RLC:
			this.state = Lock.RLT;
			break;
		case WLC:
			this.state = Lock.RLT_WLC;
			break;
		default:
			break;
			
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		
		switch (this.state) {
		case NL:
			//demander le verrou 
			this.state = Lock.WLT;
			break;
		case RLC:
			this.state = Lock.WLT;
			break;
		case WLC:
			this.state = Lock.WLT;
			break;
		default:
			break;
			
		}
		
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
