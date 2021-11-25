




public class MyRunnable implements Runnable{
    private int cs_time;

    public MyRunnable(int cs_time){
        this.cs_time = cs_time;
    }


    @Override
    public void run() {
        if (cs_time != 0){
            try {
                Thread.sleep(cs_time * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
