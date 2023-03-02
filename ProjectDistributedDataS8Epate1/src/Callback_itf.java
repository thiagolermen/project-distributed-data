import java.rmi.RemoteException;

public interface Callback_itf extends java.rmi.Remote {
    public void notifySubscriber(int object_id, Object obj) throws RemoteException;
    public Client_itf getClient() throws RemoteException;
}
