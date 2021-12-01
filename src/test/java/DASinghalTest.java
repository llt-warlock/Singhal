import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DASinghalTest {
    private final static int port = 1099;
    private final static int processesNumber = 5;
    // Initialize the setting of the RMI
    @BeforeAll
    private static void initializeThreeProcesses() {
        try{
            DASinghalRMI stub_0 = new DASinghal(0, processesNumber);
            DASinghalRMI stub_1 = new DASinghal(1, processesNumber);
            DASinghalRMI stub_2 = new DASinghal(2, processesNumber);
            DASinghalRMI stub_3 = new DASinghal(3, processesNumber);
            DASinghalRMI stub_4 = new DASinghal(4, processesNumber);
            LocateRegistry.createRegistry(port);
            // bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Singhal_0", stub_0);
            registry.bind("Singhal_1", stub_1);
            registry.bind("Singhal_2", stub_2);
            registry.bind("Singhal_3", stub_3);
            registry.bind("Singhal_4", stub_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Test MutualExclusion  1111
    // process 2 request the CS and then process 1 request the CS
    @Test
    public void TestMutualExclusion() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            DASinghalRMI process_0 = (DASinghalRMI)registry.lookup("Singhal_0");
            DASinghalRMI process_1 = (DASinghalRMI)registry.lookup("Singhal_1");
            DASinghalRMI process_2 = (DASinghalRMI)registry.lookup("Singhal_2");
            DASinghalRMI process_3 = (DASinghalRMI)registry.lookup("Singhal_3");
            DASinghalRMI process_4 = (DASinghalRMI)registry.lookup("Singhal_4");
            process_2.request();
            System.out.println("Process 2 generate a request:");
            System.out.println("Process 0 States:" + " " + process_0.getS_array());
            System.out.println("Process 1 States:" + " " + process_1.getS_array());
            System.out.println("Process 2 States:" + " " + process_2.getS_array());
            System.out.println("Token States in process 2:" + " " + process_2.getToken().getTS());
            process_1.request();
            System.out.println("Process 1 generate a request:");
            System.out.println("Process 0 States:" + " " + process_0.getS_array());
            System.out.println("Process 1 States:" + " " + process_1.getS_array());
            System.out.println("Process 2 States:" + " " + process_2.getS_array());
            System.out.println("Token States in process 1:" + " " + process_1.getToken().getTS());
            process_3.request();
            System.out.println("Process 3 generate a request:");
            System.out.println("Process 0 States:" + " " + process_0.getS_array());
            System.out.println("Process 1 States:" + " " + process_1.getS_array());
            System.out.println("Process 2 States:" + " " + process_2.getS_array());
            System.out.println("Token States in process 3:" + " " + process_3.getToken().getTS());
        } catch (RemoteException | NotBoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
