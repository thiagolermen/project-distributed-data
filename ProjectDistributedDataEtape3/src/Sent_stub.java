
public class Sent_stub extends SharedObject implements java.io.Serializable, Sent_itf {

	public Sent_stub(int id, Object object) {
		super(id, object);
	}

	public void write(String arg1) {
		Sent object = (Sent) this.obj;
		object.write(arg1);
	}

	public String read() {
		Sent object = (Sent) this.obj;
		return object.read();
	}

}
