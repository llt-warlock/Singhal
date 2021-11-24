import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DASinghal implements DASinghalRMI{
    private int pid;
    private List<Integer> n_array;
    private List<Character> s_array;
    private int numberOfProcesses;
    private Token token= null;

    public DASinghal(int pid, int numberOfProcesses){
        this.pid = pid;
        n_array = new ArrayList<>();
        s_array = new ArrayList<>();

        for (int i=0; i<numberOfProcesses; i++){
            n_array.add(0);
            s_array.add('O');
        }

    }

    public void request() throws RemoteException, NotBoundException{
        Registry registry = LocateRegistry.getRegistry("localhost");
        DASinghalRMI sender;

        s_array.set(pid, 'R');
        int temp = n_array.get(pid);
        n_array.set(pid, temp+1);

        for (int i=0; i < numberOfProcesses; i++){
            if (i != pid){
                sender = (DASinghalRMI) registry.lookup("Singhal_" + i);
                if (s_array.get(i).equals('R')){
                    // send(request; this.pid, this.N[i])
                    sender.receiveRequest(this.pid, n_array.get(pid));
                }
            }
        }

    }

    public void receiveRequest(int sender_pid, int sender_request_number) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost");
        DASinghalRMI sender = (DASinghalRMI) registry.lookup("Singhal_" + sender_pid);


        n_array.set(sender_pid, sender_request_number);

        if (s_array.get(pid).equals('E') || s_array.get(pid).equals('O')){
            s_array.set(sender_pid, 'R');
        } else if (s_array.get(pid).equals('R')) {
            if (!s_array.get(sender_pid).equals('R')){
                s_array.set(sender_pid, 'R');
                // send(request, pid, N[i]) to process with sender_pid.
                sender.receiveRequest(this.pid, this.n_array.get(pid));
            }
        } else if (s_array.get(pid).equals('H')){
            s_array.set(sender_pid, 'R');
            s_array.set(pid, 'O');
            this.token.setTS(sender_pid, 'R');
            this.token.setTN(sender_pid, sender_request_number);
            // send token to process of sender_pid.
            sender.receiveToken(this.token);
        }
    }

    public void receiveToken(Token token){
        s_array.set(pid, 'E');
        // critical section
        s_array.set(pid, 'O');
        this.token.setTS(pid, 'O');

        for (int i=0; i<numberOfProcesses; i++){
            if (i != pid){
                if (n_array.get(i) > this.token.getTN().get(i)){
                    this.token.setTN(i, n_array.get(i));
                    this.token.setTS(i, s_array.get(i));
                } else {
                    n_array.set(i, this.token.getTN().get(i));
                    s_array.set(i, this.token.getTS().get(i));
                }
            }
        }


        boolean check = true;
        for (int i=0; i<s_array.size(); i++){
            if (s_array.get(i) != 'O'){
                check = false;
            }
        }
        if(!check){
            s_array.set(pid, 'H');
        } else {
            // send token to some processes.

        }

    }


    public void setToken(Token token){
        this.token = token;
    }

}