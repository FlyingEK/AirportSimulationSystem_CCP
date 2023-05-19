/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author User
 */
public class Airport{
    private final BlockingQueue<Plane> landingQueue;
    private final BlockingQueue<Plane> departingQueue;
    private final BlockingQueue<Plane> emergencyQueue;
    private final Lock runwayLock;
    private final Semaphore runwaySemaphore = new Semaphore(1);
    private final Semaphore gate1Semaphore = new Semaphore(1);
    private final Semaphore gate2Semaphore = new Semaphore(1);
    private final Semaphore gate3Semaphore = new Semaphore(1);
    private final Semaphore groundSemaphore = new Semaphore(3);
    private Clock clock;
    private RefuelingTruck refuel;
    private Counter counter;
    private int gateNo;
    private int planeOnGround;
    
     public Airport(RefuelingTruck refuel, Counter counter) {
        landingQueue = new LinkedBlockingQueue<>();
        departingQueue = new LinkedBlockingQueue<>();
        emergencyQueue = new LinkedBlockingQueue<>();
        runwayLock = new ReentrantLock(true); // true parameter enables fair locking;
        this.refuel = refuel;
        gateNo = 0;
    }
     
    public int getPlaneOnGround(){
        return 3-groundSemaphore.availablePermits();
    }
    
    private void enterQueue( Plane plane, BlockingQueue<Plane> queue){
        // if fuel level is low, plane enters emergency landing queue
        if(plane.getFuelLevel() < plane.getMIN_FUEL_LEVEL()){
            try{
                emergencyQueue.put(plane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try{
                queue.put(plane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private void handleLand(BlockingQueue<Plane> queue, Plane plane){
        Plane nextPlane = queue.peek();
        //if the plane is the first plane in queue
//        if (nextPlane == plane) {
            System.out.printf("%s is landing on the runway...\n", plane.getPlaneName());
            try {
                Thread.sleep(2000);                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while landing on the runway.\n", plane.getPlaneName());
                return;
            }
            //Check number of planes on airport ground
            System.out.printf("%s has landed on the runway. [Current planes on airport ground: "+ getPlaneOnGround() +"]\n", plane.getPlaneName());

//        }
//         else {
//            // another plane is ahead in the queue, release the lock and try again later
//            runwaySemaphore.release();
//        }
    }
    
    private void handleDepart(BlockingQueue<Plane> queue, Plane plane){
        Plane nextPlane = queue.peek();
        //if the plane is the first plane in queue
//        if (nextPlane == plane) {
             System.out.printf("%s is departing from the runway...\n", plane.getPlaneName());
            try {
                Thread.sleep(2000);
                //remove plane from queue  
                queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while departing from the runway.\n", plane.getPlaneName());
                return;
            } 
            //release runway after departing
            runwaySemaphore.release();
            groundSemaphore.release();
            System.out.printf("%s has departed from the runway.[Current planes on airport ground: "+ getPlaneOnGround() +"]\n", plane.getPlaneName());
    //    }
//         else {
//            // another plane is ahead in the queue, release the lock and try again later
//            runwaySemaphore.release();
//        }
    }
    
    public void land(Plane plane) throws InterruptedException {
        enterQueue(plane, landingQueue);
        System.out.printf("%s is getting permission to coast to runway to land...\n", plane.getPlaneName());
        long startTime = System.currentTimeMillis();
        while (true) {
            runwaySemaphore.acquire(); // acquire the runway
            groundSemaphore.acquire(); // acquire the airport ground
            long endTime = System.currentTimeMillis();
            long waitingTime = endTime - startTime;
            try{
                // Handle emergency landing plane first
                if (!this.emergencyQueue.isEmpty()) {
                    System.out.printf("%s: EMERGENCY LANDING REQUIRED. \n", plane.getPlaneName());
                    handleLand(emergencyQueue, plane);
                    break;
                // Handle other landing plane
                } else{
                    handleLand(landingQueue, plane);
                    landingQueue.take();
                    break;
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void depart(Plane plane) throws InterruptedException {
        System.out.printf("%s is getting permission to coast to runway to depart...\n", plane.getPlaneName());
        while (true) {
            if (this.emergencyQueue.isEmpty()) {
                runwaySemaphore.acquire(); // acquire the lock
                try {
                    System.out.printf("%s is coasting to the runway to depart...\n", plane.getPlaneName());
                    Thread.sleep(1);
                    System.out.printf("%s has coasted to the runway to depart.\n", plane.getPlaneName());
                    //release gate semaphore
                    if(gateNo == 1){
                        gate1Semaphore.release();
                    }else if(gateNo ==2){
                        gate2Semaphore.release(); 
                    }else{
                        gate3Semaphore.release();
                    }
                    handleDepart(departingQueue, plane);
                    

                    // reduce the fuel level
                    Random rand = new Random();
                    int fuelUsage = rand.nextInt();
                    int originalFuelLevel = plane.getFuelLevel();
                    plane.setFuelLevel(originalFuelLevel - fuelUsage);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }        
        }
    }
    
    private void refillSupplies(Plane plane){
        System.out.printf("%s is getting supplies refill...\n", plane.getPlaneName());
        try {
            Thread.sleep(2000);                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s was interrupted while getting supplies refill.\n", plane.getPlaneName());
            return;
        }  
    }
    
    private void cleaning(Plane plane){
        System.out.printf("%s is getting aircraft cleaning...\n", plane.getPlaneName());
        try {
            Thread.sleep(2000);                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s was interrupted while getting aircraft cleaning.\n", plane.getPlaneName());
            return;
        }  
    }
    
    public void occupyGate(Plane plane) {
        try{
            System.out.printf("%s is getting permission to coast to the gate...\n", plane.getPlaneName());
            if (gate1Semaphore.tryAcquire()) {
                System.out.printf("%s is coasting to Gate 1...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 1.\n", plane.getPlaneName());
                gateNo = 1;
            } else if (gate2Semaphore.tryAcquire()) {
                System.out.printf("%s is coasting to Gate 2...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 2.\n", plane.getPlaneName());
                gateNo = 2;
            } else if (gate3Semaphore.tryAcquire()) {
                System.out.printf("%s is coasting to Gate 3...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 3.\n", plane.getPlaneName());
                gateNo = 3;
            } else {
                System.out.printf("%s is waiting for a gate...\n", plane.getPlaneName());
                occupyGate(plane);
            }
            runwaySemaphore.release();
            plane.disembark();
            refillSupplies(plane);
            refuel.refuelPlane(plane);
            plane.embark();
            departingQueue.add(plane);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s was interrupted while coasting to the gate.\n", plane.getPlaneName());
            return;
        }
    }

        // Finish all current process and depart all the planes, then close the airport
//    public void closeAirport() throws InterruptedException {
//        isRunning.set(false);
//        while (numPlanesBeingServiced > 0) {
//            TimeUnit.SECONDS.sleep(1);
//        }
//        System.out.println("Airport is closed.");
//    }
}
