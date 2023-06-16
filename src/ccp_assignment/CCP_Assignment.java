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
        RefuelingTruck truck= new RefuelingTruck();
        Airport airport = new Airport(clock,truck);
        Counter c = new Counter();
        
        Report report = new Report(airport,clock,c);
        clock.start();
        
      //  truck.start();

        Plane plane1 = new Plane("Plane 1", c, airport, clock);
        Plane plane2 = new Plane("Plane 2", c, airport, clock);
        Plane plane3 = new Plane("Plane 3", c, airport, clock);
        Plane plane4 = new Plane("Plane 4", c, airport, clock);
        Plane plane5 = new Plane("Plane 5", c, airport, clock);
        Plane plane6 = new Plane("Plane 6", c, airport, clock);
        
        // The first round of landing at the interval time of 0, 1, 2, 3 randomly
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
            plane1.join();plane2.join();plane3.join();plane4.join();plane5.join();plane6.join();
            report.start();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

       }
}
