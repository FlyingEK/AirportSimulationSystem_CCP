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
 
 class Plane extends Thread{
    private String planeName;
    private int fuelLevel;
    private static final int MIN_FUEL_LEVEL= 10;
    private static final int MAX_FUEL_LEVEL = 100;
    private int passengerNum;
    private Counter counter;
    private static int planeNum = 0;
    private RefuelingTruck refuelTruck;
    private Airport airport;
    private WaitingTime waitingTime;
    public Boolean closingTime;
    private Clock clock;
    //private max, min, total Waiting time
    
    public Plane(){
        closingTime = false;
    }
    
    public Plane(String planeName, Counter counter, Airport airport, Clock clock){
//        PassengerCounter passenger = new PassengerCounter();
        Random rand = new Random();
        this.planeName = planeName;
        fuelLevel = MAX_FUEL_LEVEL;
        this.counter = counter;
        planeNum++;
        RefuelingTruck refuelingTruck = new RefuelingTruck();
        this.airport = airport; // shared airport
        //closingTime = false;
        this.clock = clock;
        waitingTime = new WaitingTime();
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
    
    public void setClosingTime(Boolean closingTime) {
        this.closingTime = closingTime;
    }
    
    private void updateWaitingTime(long time){
        // Update max and min waiting time
        if (time > waitingTime.getMaxWaitingTime()){
            waitingTime.setMaxWaitingTime(time);
        }else if (time < waitingTime.getMinWaitingTime()){
            waitingTime.setMinWaitingTime(time);
        }
        waitingTime.addTotalWaitingTime(time);
        waitingTime.addWaitCount();
    }
    
    //wip : at which gate.
    public synchronized void disembark() {
        Random rand = new Random();
        passengerNum = rand.nextInt(5);
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
        counter.add(passengerNum);
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
    public synchronized void embark() {
        Random rand = new Random();
        //change
        passengerNum = rand.nextInt(5);
        int j = 1;
        System.out.printf("%s is embarking " +passengerNum+ " passengers...\n", planeName);
        for (int i = passengerNum; i > 0 ; i--) {
            try {
                Thread.sleep(50);
                System.out.println(planeName+ ": Passenger " + j++ + " has embarked.");
               
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while embarking passengers.\n", planeName);
                return;
            }
        }
        counter.add(passengerNum);
        System.out.println(planeName+ ": All " +passengerNum+ " passengers have embarked.");
    }
    
    public void run() {
        while(!clock.closingTime){
            try{
                airport.land(this);
                airport.occupyGate(this);
                airport.depart(this);
            }catch(InterruptedException e){
                e.printStackTrace();;
            }
        }
//            // Land on the runway
//            System.out.println("Aircraft " + id + " landing on the runway...");
//            Thread.sleep(5000);
//            
//            // Coast to assigned gate
//            getGate();
//           embark();
            // Disembark passengers
            //disembark();
            
            // Refill supplies and fuel


        }
    }

