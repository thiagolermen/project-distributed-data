import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Callback extends UnicastRemoteObject implements Callback_itf{
    private Client_itf client;
    public Callback(Client_itf client, int objectHasChanged) throws RemoteException {
        this.client = client;
    }

    public void notifySubscriber(int objectId, Object obj) throws RemoteException {
        this.client.notificationReception(objectId, obj);
    }

    public Client_itf getClient() throws RemoteException {
        return this.client;
    }
}
