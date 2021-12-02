import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DASinghal extends UnicastRemoteObject implements DASinghalRMI, Runnable{
    private int pid;
    Lock lock_c = new ReentrantLock();
    Lock lock_t = new ReentrantLock();
    Lock lock_n = new ReentrantLock();
    Lock lock_s = new ReentrantLock();
    private List<Integer> n_array;
    private List<Character> s_array;
    private int numberOfProcesses;
    private Token token= null;
    private int cs_time;

    public DASinghal(int pid, int numberOfProcesses) throws RemoteException, NotBoundException, InterruptedException{
        this.numberOfProcesses = numberOfProcesses;
        this.pid = pid;
        n_array = new ArrayList<>();
        s_array = new ArrayList<>();
        if (pid == 0) {
            s_array.add('H');
            for (int i=1; i < this.numberOfProcesses; i++) {
                s_array.add('O');
            }
            this.token = new Token(this.numberOfProcesses);
        } else {
            for (int i=0; i<this.numberOfProcesses; i++){
                if (i < pid){
                    s_array.add('R');
                } else {
                    s_array.add( 'O');
                }
            }
        }

        for (int i=0; i<this.numberOfProcesses; i++){
            n_array.add(0);
        }

        this.cs_time = ThreadLocalRandom.current().nextInt(1, 5 + 1);

    }
    public List<Character> copyS_array(){
        List<Character> copy = new ArrayList<>();
        for(int i=0; i < s_array.size(); i++){
            copy.add(s_array.get(i));
        }
        return copy;

    }

    public void request() throws RemoteException, NotBoundException, InterruptedException {

        Registry registry = LocateRegistry.getRegistry("localhost");
        DASinghalRMI sender;
        lock_c.lock();
        List<Character> copy = copyS_array();

        lock_s.lock();
        s_array.set(this.pid, 'R');

        lock_n.lock();
        int temp = n_array.get(pid);
        n_array.set(this.pid, temp+1);


        for (int i=0; i < this.numberOfProcesses; i++){
            if (i != this.pid){
                sender = (DASinghalRMI) registry.lookup("Singhal_" + i);
//                System.out.println("agagagag:"+i);

                if (copy.get(i).equals('R')){
                    // send(request; this.pid, this.N[i])

                    sender.receiveRequest(this.pid, n_array.get(this.pid));

                }
//                System.out.println("bbbbbb:"+i);

            }

        }
        lock_c.unlock();
        lock_n.unlock();
        lock_s.unlock();
    }

    public void receiveRequest(int sender_pid, int sender_request_number) throws RemoteException, NotBoundException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry("localhost");
        DASinghalRMI sender = (DASinghalRMI) registry.lookup("Singhal_" + sender_pid);
//        System.out.println("s_array1:"+s_array);


        lock_n.lock();
        lock_s.lock();
        lock_t.lock();

        n_array.set(sender_pid, sender_request_number);

        if (s_array.get(this.pid).equals('E') || s_array.get(this.pid).equals('O')){
            s_array.set(sender_pid, 'R');
        } else if (s_array.get(this.pid).equals('R')) {
            if (!s_array.get(sender_pid).equals('R')){
                s_array.set(sender_pid, 'R');
                // send(request, pid, N[i]) to process with sender_pid.
                sender.receiveRequest(this.pid, this.n_array.get(this.pid));
            }
        } else if (s_array.get(this.pid).equals('H')){
            s_array.set(sender_pid, 'R');
            s_array.set(this.pid, 'O');
            this.token.setTS(sender_pid, 'R');
            this.token.setTN(sender_pid, sender_request_number);
            // send token to process of sender_pid.
//            System.out.println("s_array2:"+s_array);
//            System.out.println("token:"+this.token.getTS());

            sender.receiveToken(this.pid, this.token);
            this.token = null;
        }

        lock_t.unlock();
        lock_s.unlock();
        lock_n.unlock();

    }

    public void receiveToken(int sender_pid, Token token) throws RemoteException, NotBoundException, InterruptedException {
//        System.out.println("2222:"+token.getTS());

        Registry registry = LocateRegistry.getRegistry("localhost");
        DASinghalRMI sender = (DASinghalRMI) registry.lookup("Singhal_" + sender_pid);

        lock_s.lock();
        lock_n.lock();
        lock_t.lock();

        s_array.set(this.pid, 'E');
//        System.out.println("1111:"+s_array);
        // critical section
//        System.out.println("aaaaaa");
        this.execute_cs(this.cs_time);
//        System.out.println("bbbb");
        s_array.set(this.pid, 'O');
//        System.out.println("TOKEN:"+ token.getTS());
        token.setTS(this.pid, 'O');

        for (int i=0; i<this.numberOfProcesses; i++){
            if (i != this.pid){
                if (n_array.get(i) > token.getTN().get(i)){
                    token.setTN(i, n_array.get(i));
                    token.setTS(i, s_array.get(i));
                } else {
                    n_array.set(i, token.getTN().get(i));
                    s_array.set(i, token.getTS().get(i));
                }
            }
        }


        boolean check = true;
        for (int i=0; i<s_array.size(); i++){
            if (s_array.get(i) != 'O'){
                check = false;
                System.out.println("check1");
                break;
            }
        }
        if(check){
//            System.out.println("check2");
            s_array.set(this.pid, 'H');
        } else {
            // send token to some processes.
            if (s_array.get(sender_pid).equals('R')){
                sender.receiveToken(this.pid, token);
                token = null;
            }

        }
//        System.out.println("11111"+token.getTS());
//        System.out.println("final"+s_array);
        setToken(token);

        lock_s.lock();
        lock_n.lock();
        lock_t.lock();

    }


    public void setToken(Token token){
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }

    public void execute_cs(int cs_time) throws InterruptedException {
        if (cs_time != 0){
            Thread.sleep(cs_time * 1000);
        }
    }

    public List<Integer> getN_array() {
        return n_array;
    }

    public List<Character> getS_array() {
        return s_array;
    }

    @Override
    public void run() {
        try {
            this.request();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
