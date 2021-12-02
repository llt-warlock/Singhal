import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class MyRunnable implements Runnable{
    private DASinghalRMI singhal;

    public MyRunnable(DASinghalRMI singhal){
        this.singhal = singhal;
    }

    public void run(){
        try {
            this.singhal.request();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
