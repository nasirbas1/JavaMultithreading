package ThreadCreation.NewRunnable;

public class Main {
    public static void main(String[] args) throws InterruptedException{
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("We are in"+ Thread.currentThread().getName());
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("We are in"+ Thread.currentThread().getName());
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("We are in"+ Thread.currentThread().getName());
            }
        });
        System.out.println("We are in" + Thread.currentThread().getName());
        thread1.setName("Thread 1");
        thread2.setName("Thread 2");
        thread3.setName("Thread 3");
        thread2.setPriority(Thread.MAX_PRIORITY);
        thread1.setPriority(3);
        thread3.setPriority(Thread.NORM_PRIORITY);
        thread2.start();
        thread3.start();
        thread1.start();
        System.out.println("We are in" + Thread.currentThread().getName());
        Thread.sleep(10000);
    }
}
