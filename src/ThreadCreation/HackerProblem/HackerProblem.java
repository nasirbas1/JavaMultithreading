// There are 3 hackers who try to break into a password protected vault. Another police threat starts a countdown
// and if police thread catches them it shows which all hackers were and also concurrently show countdown
// timer

package ThreadCreation.HackerProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HackerProblem {
    public static final int MAX_PASSWORD = 9999;
    public static void main(String[] args) throws InterruptedException{
        Random random = new Random();
        int pass = random.nextInt(MAX_PASSWORD);
        System.out.println("Pass is "+ pass);
        Vault vault = new Vault(pass);

        List<Thread> threads = new ArrayList<>();

        threads.add(new AscendingHackerThread(vault));
        threads.add(new DescendingHackerThread(vault));
        threads.add(new PoliceThread());

        for(Thread thread: threads){
            thread.start();
        }

    }

    private static class Vault{
        private int password;
        public Vault(int password){
            this.password = password;
        }

        public boolean isCorrectPassword(int guess){
            try {
                Thread.sleep(5);
            }catch (InterruptedException e){
                System.out.println(e);
            }
            return this.password == guess;
        }
    }

    // Generic hacker thread to encapsulate functionality
    private static abstract class HackerThread extends Thread{
        protected Vault vault;

        public HackerThread(Vault vault){
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start(){
            System.out.println("Starting thread" + this.getName());
            super.start();
        }
    }

    private static class AscendingHackerThread extends HackerThread{
        public AscendingHackerThread(Vault vault){
            super(vault);
        }
        @Override
        public void run(){
            for( int guess = 0; guess < MAX_PASSWORD; guess ++){
                System.out.println("Trying in " + this.getClass().getSimpleName() + " with guess "+ guess);
                if(vault.isCorrectPassword(guess)){
                    System.out.println(this.getName() +" guessed the password "+ guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class DescendingHackerThread extends HackerThread{
        public DescendingHackerThread(Vault vault){
            super(vault);
        }
        @Override
        public void run(){
            for( int guess = MAX_PASSWORD; guess >= 0 ; guess --){
                System.out.println("Trying in " + this.getClass().getSimpleName() + " with guess "+ guess);
                if(vault.isCorrectPassword(guess)){
                    System.out.println(this.getName() +" guessed the password "+ guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class PoliceThread extends Thread{
        @Override
        public void run(){
            for(int i=10; i>0; i--){
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    System.out.println(e);
                }
                System.out.println("Countdown to " + i);
            }

            System.out.println("Gameover for hackers");
            System.exit(0);
        }
    }
}
