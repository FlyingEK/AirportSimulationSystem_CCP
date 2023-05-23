/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;

/**
 *
 * @author User
 */
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Report extends Thread {
    private Airport airport;
    private Clock clock;
    private Counter c;
    private Plane plane;
    
    Report(Airport airport, Clock clock, Counter c){
        this.airport = airport;
        this.clock = clock;
        this.c = c;
    }

    
    public void run(){
        //keeping it awake
//        while(!clock.closingTime){
//            try{
//                Thread.sleep(5000);
//            }catch(InterruptedException e){
//                e.printStackTrace();
//            }
//        }
        while (true){
            if(clock.closingTime && airport.getPlaneOnGround() == 0){
                System.out.println("-------Notification: All planes has left the airport.-------");
                System.out.println("-------REPORT-------");
                System.out.println("---WAITING TIME REPORT---");
                airport.getPlanesWaitingTime();
                System.out.println("---PASSENGER REPORT---");
                System.out.println("The total passenger boarded: "+c.getTotalPassenger());
                return;
            }
        }
        
    }
    
}
