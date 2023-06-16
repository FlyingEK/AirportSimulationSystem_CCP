/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;

/**
 *
 * @author User
 */


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
        while (true){
            if(clock.closingTime && airport.getPlaneOnGround() == 0){
                System.out.println("-------Notification: All planes has left the airport.-------");
                System.out.println("-------STATISTIC REPORT-------");
                System.out.println("---AIRPORT REPORT---");
                System.out.println("The current planes on the airport ground: "+ airport.getPlaneOnGround());
                System.out.println("Number of plane at Gate 1: " + airport.getPlaneNoGate1());
                System.out.println("Number of plane at Gate 2: " + airport.getPlaneNoGate2());
                System.out.println("Number of plane at Gate 3: " + airport.getPlaneNoGate3());
                System.out.println("---PLANE/PASSENGER REPORT---");
                System.out.println("The planes served: "+airport.getTotalPlane());
                System.out.println("The total passenger boarded: "+c.getTotalPassenger());
                System.out.println("---WAITING TIME REPORT---");
                airport.getPlanesWaitingTime();
                return;
            }
        }
        
    }
    
}
