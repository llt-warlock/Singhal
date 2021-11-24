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

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException {
        LocateRegistry.createRegistry(PORT);
        creatingProcesses(numberOfProcesses);
    }

    public static void creatingProcesses(int numberOfProcesses) throws RemoteException, AlreadyBoundException, NotBoundException {
        registry = LocateRegistry.getRegistry();
        for (int i=0; i<numberOfProcesses; i++){
            DASinghal stub = new DASinghal(i, numberOfProcesses);
            registry.bind("Singhal_"+i, stub);
            System.out.println(stub.getS_array());
        }

        registry = LocateRegistry.getRegistry("localhost");

        for (int i=0; i<numberOfProcesses; i++){
            DASinghalRMI process_temp = (DASinghalRMI)registry.lookup("Singhal_"+i);

        }

    }
}