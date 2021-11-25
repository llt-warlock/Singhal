import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class DASinghalMain {

    public static int PORT = 1099;
    public static List<DASinghal> stub_list = new ArrayList<>();
    public static Registry registry;
    public static int numberOfProcesses = 3;

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, InterruptedException {
        LocateRegistry.createRegistry(PORT);
        creatingProcesses(numberOfProcesses);
    }

    public static void creatingProcesses(int numberOfProcesses) throws RemoteException, AlreadyBoundException, NotBoundException, InterruptedException {
        registry = LocateRegistry.getRegistry();
        for (int i=0; i<numberOfProcesses; i++){
            DASinghalRMI stub = new DASinghal(i, numberOfProcesses);
            registry.bind("Singhal_"+i, stub);
        }

        registry = LocateRegistry.getRegistry("localhost");

//        for (int i=0; i<numberOfProcesses; i++){
//            DASinghalRMI process_temp = (DASinghalRMI)registry.lookup("Singhal_"+i);
//        }
        DASinghalRMI process_0 = (DASinghalRMI)registry.lookup("Singhal_"+0);
        DASinghalRMI process_1 = (DASinghalRMI)registry.lookup("Singhal_"+1);
        DASinghalRMI process_2 = (DASinghalRMI)registry.lookup("Singhal_"+2);
        process_2.request();
        System.out.println("Process 2 generate a request:");
        System.out.println("Process 0 States:" + " " + process_0.getS_array());
        System.out.println("Process 1 States:" + " " + process_1.getS_array());
        System.out.println("Process 2 States:" + " " + process_2.getS_array());
        System.out.println("Token States in process 2:" + " " + process_2.getToken().getTS());
//        process_1.request();
//        System.out.println("Process 1 generate a request:");
//        System.out.println("Process 0 States:" + " " + process_0.getS_array());
//        System.out.println("Process 1 States:" + " " + process_1.getS_array());
//        System.out.println("Process 2 States:" + " " + process_2.getS_array());
//        System.out.println("Token States in process 1:" + " " + process_1.getToken().getTS());
    }
}