/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
/**
 *
 * @author User
 */
class Counter{
    AtomicInteger totalPassenger = new AtomicInteger(0);
    
    synchronized void add(int passengerNo) {
        //read the value and increment it
        totalPassenger.getAndAdd(passengerNo);
    }
    
    public int getTotalPassenger(){
        return totalPassenger.get();
    }
}
