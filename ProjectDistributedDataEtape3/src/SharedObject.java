import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private static final long serialVersionUID = 899922328733830816L;
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
		boolean askToServer = false;
		
		synchronized (this) {
			
			// Waits while a client has the lock is taken
			while (this.waiting) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			switch (this.state) {
			case NL:
				// ask for the lock
				askToServer = true;
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
		
		if (askToServer) {
			this.obj = Client.lock_read(this.id);
		}
		
	}

	/**
	 * Invoked by the user program on the client node once it demands for a write lock
	 */
	public void lock_write() { 
		
		// Determines if the Client should demand the server for the write lock
		boolean askToServer = false;
		
		synchronized (this) {
			
			// Waits while a client has the lock is taken
			while (this.waiting) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			switch (this.state) {
			case NL:
				// ask for the lock
				askToServer = true;
				this.state = Lock.WLT;
				break;
			case RLC:
				// ask for the lock
				askToServer = true;
				this.state = Lock.WLT;
				break;
			case WLC:
				this.state = Lock.WLT;
				break;
			default:
				break;
				
			}
		}
		
		if (askToServer) {
			this.obj = Client.lock_write(this.id);
		}
		
	}


	/**
	 * 	Invoked by the user program on the client node once it demands for an unlock lock
	 */
	public synchronized void unlock() {
		
		switch (this.state) {
		case RLT:
			// ask for the lock 
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
			this.notify();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * Callback invoked remotely by the server
	 * It is a blocking call for clients that are demanding the lock
	 * @return the general reference to object of the application
	 */
	public synchronized Object reduce_lock() {
		
		// Blocks other processes
		this.waiting = true;
		
		switch (this.state) {
		
		case WLT:
			
			// If there's a process that is currently writing the demands waits
			while (this.state == Lock.WLT) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		case WLC:
			this.state = Lock.RLC;
			break;
			
		case RLT_WLC:
			this.state = Lock.RLT;
			break;
			
		default:
			break;
			
		}
		
		// Unblocks other processes
		this.waiting = false;
		
		try {
			this.notify();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.obj;
		
	}

	
	/**
	 * Callback invoked remotely by the server
	 */
	public synchronized void invalidate_reader() {
		
		this.waiting = true;
		
		switch (this.state) {
		
		case RLT:
			
			while (this.state == Lock.RLT) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		case RLC:
			this.state = Lock.NL;
			break;
			
		default:
			break;
			
		}
		
		this.waiting = false;
		
		try {
			this.notify();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @return
	 */
	public synchronized Object invalidate_writer() {
		
		this.waiting = true;
		
		switch (this.state) {
		
		case WLT:
			
			while (this.state == Lock.WLT) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		case WLC:
			this.state = Lock.NL;
			break;
			
		case RLT_WLC:
			while (this.state == Lock.RLT_WLC) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.state = Lock.NL;
			break;
			
		default:
			break;
			
		}
		
		this.waiting = false;
		
		try {
			this.notify();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.obj;
		
	}
	
	/**
	 * @return the reference of the stub of the object with this.id
	 * @throws ObjectStreamException
	 */
	protected Object readResolve() throws ObjectStreamException{
		
		// If the current call of deserialization is type Server to client, just return the current object
		if (TypeOfDeserialization.getTypeOfDeserialization() == TypeOfDeserialization.TypeDes.SERVER_CLIENT) {
			return this;	
		}
		
		// Check if object stub already exists, if not creates a new one and returns it
		SharedObject so = Client.getStub(this.id);
		if (so == null) {
			so = Client.generateStub(getId(), so);
		}
		
		return so;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
