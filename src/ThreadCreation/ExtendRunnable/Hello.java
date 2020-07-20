package ThreadCreation.ExtendRunnable;

public class Hello {
    public static void main(String[] args) throws InterruptedException{
       Thread thread = new NewThread();
       thread.setName("Hulaaa");
       thread.start();
    }

    public static class NewThread extends Thread{
        @Override
        public void run(){
            System.out.println("Hello from " + this.getName());
        }
    }
}

