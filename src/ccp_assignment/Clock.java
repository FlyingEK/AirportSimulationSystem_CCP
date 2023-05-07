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
public class Clock {
    public Clock(Barber barber, CustomerGenerator cg){
        this.barber = barber;
        this.cg = cg;
    }
    
    public void run(){
        try{
            sleep(30000);
            notifyClosingTime();
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }
    public synchronized void notifyClosingTime(){
        System.out.println("Clock: Barber shop is closed. No customers can enter");
        barber.closingTime=true;
        cg.closingTime=true;
    }
}
