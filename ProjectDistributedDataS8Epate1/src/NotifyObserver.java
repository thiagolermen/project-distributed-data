import java.util.Observable;
import java.util.Observer;

public class NotifyObserver implements Observer {
	public void observe(Observable o) {
	  o.addObserver(this);
	}
  
	@Override
	public void update(Observable o, Object arg) {
	  int objectHasChanged = ((ObjectObservable) o).getObjectHasChanged();
	  int changementCounter = ((ObjectObservable) o).getChangementCounter();
	  System.out.println("Client got notification of changement of object " + objectHasChanged + " - Number of changements: " + changementCounter);
	}
}
