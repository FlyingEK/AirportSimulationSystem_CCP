/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ccp_assignment;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.Semaphore;
/**
 *
 * @author User
 */
public class CCP_Assignment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Random rand = new Random();    
        Clock clock = new Clock();
        Airport airport = new Airport(clock);
        Counter c = new Counter();
        Report report = new Report(airport,clock,c);
        clock.start();
        report.start();
        Plane plane1 = new Plane("Plane 1", c, airport, clock);
        Plane plane2 = new Plane("Plane 2", c, airport, clock);
        Plane plane3 = new Plane("Plane 3", c, airport, clock);
        Plane plane4 = new Plane("Plane 4", c, airport, clock);
        Plane plane5 = new Plane("Plane 5", c, airport, clock);
        Plane plane6 = new Plane("Plane 6", c, airport, clock);
        
        try{
            plane1.start();
            TimeUnit.SECONDS.sleep(rand.nextInt(4));
            plane2.start();
            TimeUnit.SECONDS.sleep(rand.nextInt(4));
            plane3.start();
            TimeUnit.SECONDS.sleep(rand.nextInt(4));
            plane4.start();
            TimeUnit.SECONDS.sleep(rand.nextInt(4));
            plane5.start();
            TimeUnit.SECONDS.sleep(rand.nextInt(4));
            plane6.start();
            TimeUnit.SECONDS.sleep(rand.nextInt(4));
        }catch(InterruptedException e){
            e.printStackTrace();
        }
//
//        try{
//            plane1.start();
//            TimeUnit.SECONDS.sleep(3);
//            plane2.start();
//            TimeUnit.SECONDS.sleep(3);
//            plane3.start();
//            TimeUnit.SECONDS.sleep(3);
//            plane4.start();
//            TimeUnit.SECONDS.sleep(3);
//            plane5.start();
//            TimeUnit.SECONDS.sleep(3);
//            plane6.start();
//            TimeUnit.SECONDS.sleep(3);
//        }catch(InterruptedException e){
//            e.printStackTrace();
//        }
        // A plane thread start every 1-3 seconds
//        ExecutorService executorService = Executors.newFixedThreadPool(6);
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
//        long initialDelay = 0; // Start immediately
//        long delay = 3; // 3 seconds between landings
//
//        for (int i = 1; i <= 6; i++) {
//            Plane plane = new Plane("Plane " + i, c);
//            scheduledExecutorService.scheduleAtFixedRate(plane, initialDelay, delay, TimeUnit.SECONDS);
//            executorService.execute(plane); // Start the plane thread
////
////executorService.execute(plane); // Start the plane thread
        // check plane num in airport if()
        //System.out.print("" + c.getTotalPassenger());
            while(true){
                try{
                    Thread.sleep(1000);
                    
                }catch(InterruptedException e){
                e.printStackTrace();
                }
            }
       }
}
