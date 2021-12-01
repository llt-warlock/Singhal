import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Token implements Serializable {
    private int numberOfProcesses;
    private List<Integer> TN;
    private List<Character> TS;

    public Token(int numberOfProcesses){
        this.numberOfProcesses = numberOfProcesses;
        TN = new ArrayList<>();
        TS = new ArrayList<>();
        for (int i=0; i < numberOfProcesses; i++){
            TN.add(0);
            TS.add('O');
        }
    }

    public void setTS(int pid, char value){
        this.TS.set(pid, value);
    }

    public void setTN(int pid, int value){
        this.TN.set(pid, value);
    }

    public List<Integer> getTN(){
        return this.TN;
    }

    public List<Character> getTS(){
        return this.TS;
    }




}
