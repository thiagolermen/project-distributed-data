
public class Sentence_stub extends SharedObject implements java.io.Serializable, Sentence_itf {

	public Sentence_stub(int id, Object object) {
		super(id, object);
	}

	public void write(String arg1) {
		Sentence object = (Sentence) this.obj;
		object.write(arg1);
	}

	public String read() {
		Sentence object = (Sentence) this.obj;
		return object.read();
	}

}
