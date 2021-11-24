import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DASinghalRMI extends Remote {

    public void request() throws RemoteException, NotBoundException, InterruptedException;
    public void receiveRequest(int sender_pid, int sender_request_number) throws RemoteException, NotBoundException, InterruptedException;
    public void receiveToken(int sender_pid, Token token) throws RemoteException, NotBoundException, InterruptedException;

}
