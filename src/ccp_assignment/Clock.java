/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;

import static java.lang.Thread.sleep;

/**
 *
 * @author User
 */
public class Clock extends Thread{
    volatile Boolean closingTime;
    private Plane plane;
    public Clock(){
        //this.plane = plane;
        closingTime = false;
    }
    
    public void run(){
        try{
            //rmb to change
            sleep(60000);
            notifyClosingTime();
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }
    public synchronized void notifyClosingTime(){
        System.out.println("-------Notification: Airport is closed. No more incoming planes.-------");
        System.out.println("-------Allowing planes on the airport ground to finsih their process and depart-------");
        closingTime = true;
    }
}
