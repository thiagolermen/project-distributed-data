import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private static final long serialVersionUID = 899922328733830816L;
	private Integer id;
	public boolean waiting;
	public Object obj;
	public Lock state;

	public SharedObject(int id, Object obj){
        this.id = id;
        this.waiting = false;
        this.obj = obj;
		this.state = Lock.NL;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		
		boolean askToServer = false;
		
		synchronized (this) {
		
			while (this.waiting) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			switch (this.state) {
			case NL:
				//demander le verrou
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

	// invoked by the user program on the client node
	public void lock_write() {
		
		boolean askToServer = false;
		
		synchronized (this) {
			
			while (this.waiting) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			switch (this.state) {
			case NL:
				//demander le verrou
				askToServer = true;
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
		
		if (askToServer) {
			this.obj = Client.lock_write(this.id);
		}
		
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		
		switch (this.state) {
		case RLT:
			//demander le verrou 
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

	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		
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
			
			this.state = Lock.RLC;
			break;
			
		case RLT_WLC:
			this.state = Lock.RLT;
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

	// callback invoked remotely by the server
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
