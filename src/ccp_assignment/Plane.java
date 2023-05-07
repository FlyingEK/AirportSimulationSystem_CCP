/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author User
 */
class PassengerCounter{
     volatile AtomicInteger totalPassenger = new AtomicInteger(0);
    synchronized void increment() {
        //read the value and increment it
        totalPassenger.getAndIncrement();
    }

    public int getTotalPassenger(){
        return totalPassenger.get();
    }
}
 
 class Plane extends Thread{
    private String planeName;
    private int fuelLevel;
    private static final int MIN_FUEL_LEVEL= 10;
    private static final int MAX_FUEL_LEVEL = 100;
    private int passengerNum;
    private PassengerCounter passenger;
    private static int planeNum = 0;
    private RefuelingTruck refuelTruck;
    AtomicInteger totalPassenger = new AtomicInteger(0);
    
    public Plane(String planeName,PassengerCounter c){
//        PassengerCounter passenger = new PassengerCounter();
        Random rand = new Random();
        this.planeName = planeName;
        passengerNum = rand.nextInt(50);
        passenger = c;
        planeNum++;
        RefuelingTruck refuelingTruck = new RefuelingTruck();
    }
    
    public static int getPlaneNum(){
        return planeNum;
    }
    
    public String getPlaneName(){
        return planeName;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public static int getMIN_FUEL_LEVEL() {
        return MIN_FUEL_LEVEL;
    }

    public static int getMAX_FUEL_LEVEL() {
        return MAX_FUEL_LEVEL;
    }
   
    
    public void getGate(){
        System.out.printf("%s is coasting to the gate..\n", planeName);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s was interrupted while coasting to the gate.\n", planeName);
            return;
        }
        // Starts to disembark passenger and get supply refill
        disembark();
        refillSupplies();
    }
    
    //wip : at which gate.
    private synchronized void disembark() {
        int leftPassengerNum = passengerNum;
        System.out.printf("%s is disembarking " +passengerNum+ " passengers...\n", planeName);
        
        if(leftPassengerNum > 0){
            for (int i = 0; i < passengerNum ; i++) {
                try {
                    Thread.sleep(50);
                    System.out.println(planeName+ ": Passenger " + (i+1) + " has disembarked.");
                    leftPassengerNum--;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.printf("%s was interrupted while disembarking passengers.\n", planeName);
                    return;
                }
            }   
        }
        
        System.out.println(planeName+ ": All " +passengerNum+ " passengers have disembarked.");
    }

    private void refillSupplies() {
        System.out.printf("%s is refilling supplies and cleaning the aircraft.\n", planeName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s was interrupted while refilling supplies and cleaning the aircraft.\n", planeName);
            return;
        }
        System.out.printf("%s has finished refilling supplies and cleaning the aircraft.\n", planeName);
    }

    // wip
    private synchronized void embark() {
        Random rand = new Random();
        passengerNum = rand.nextInt(50);
        int j = 1;
        System.out.printf("%s is embarking " +passengerNum+ " passengers...\n", planeName);
        for (int i = passengerNum; i > 0 ; i--) {
            try {
                Thread.sleep(50);
                System.out.println(planeName+ ": Passenger " + j++ + " has embarked.");
               // totalPassenger.getAndIncrement();
               passenger.increment();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while embarking passengers.\n", planeName);
                return;
            }
        }
        System.out.println(planeName+ ": All " +passengerNum+ " passengers have embarked.");
        
    }

    public AtomicInteger getTotalPassenger() {
        return totalPassenger;
    }
    
    public void run() {
          
            // Request landing permission
//            System.out.println("Aircraft " + id + " requesting landing permission...");
//            Airport.requestLandingPermission(this);
//            
//            // Land on the runway
//            System.out.println("Aircraft " + id + " landing on the runway...");
//            Thread.sleep(5000);
//            
//            // Coast to assigned gate
//            getGate();
           embark();
            // Disembark passengers
            //disembark();
            
            // Refill supplies and fuel


    }
}
