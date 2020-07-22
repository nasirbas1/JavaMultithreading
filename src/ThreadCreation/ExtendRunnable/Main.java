package ThreadCreation.ExtendRunnable;

public class Main {
    public static void main(String[] args) throws InterruptedException{
       Thread thread = new NewThread();
       thread.setName("New Runnable Thread");
       thread.start();
    }

    public static class NewThread extends Thread{
        @Override
        public void run(){
            System.out.println("Hello from " + this.getName());
        }
    }
}

