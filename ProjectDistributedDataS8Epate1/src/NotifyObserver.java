import java.util.Observable;
import java.util.Observer;

public class NotifyObserver implements Observer {
	public void observe(Observable o) {
	  o.addObserver(this);
	}
  
	@Override
	public void update(Observable o, Object arg) {
	  int objectHasChanged = ((ObjectObservable) o).getObjectHasChanged();
	  int changementCounter = Client.getChangementCounter();
	  System.out.println("New object modification detected - Number of ignored updates: " + changementCounter);
	}
}
