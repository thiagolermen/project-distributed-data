import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Callback extends UnicastRemoteObject implements Callback_itf{
    private Client client;
    public Callback(Client client) throws RemoteException {
        this.client = client;
    }

    public void notifySubscriber(int object_id) throws RemoteException {

    }
}
