




public class MyRunnableCS implements Runnable{
    private int time;

    public MyRunnableCS(int time){
        this.time = time;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
