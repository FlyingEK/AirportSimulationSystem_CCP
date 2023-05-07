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
public class Airport {
    private final BlockingQueue<Plane> landingQueue;
    private final BlockingQueue<Plane> departingQueue;
    private final BlockingQueue<Plane> emergencyQueue;
    private final Lock runwayLock;
    private final Semaphore gate1Semaphore = new Semaphore(1);
    private final Semaphore gate2Semaphore = new Semaphore(1);
    private final Semaphore gate3Semaphore = new Semaphore(1);
    private final Semaphore groundSemaphore = new Semaphore(3);
    
     public Airport() {
        landingQueue = new LinkedBlockingQueue<>();
        departingQueue = new LinkedBlockingQueue<>();
        emergencyQueue = new LinkedBlockingQueue<>();
        runwayLock = new ReentrantLock(true); // true parameter enables fair locking
    }
    
    private void enterQueue(Plane plane, BlockingQueue<Plane> queue){
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
        if (nextPlane == plane) {
            System.out.printf("%s is landing on the runway...\n", plane.getPlaneName());
            try {
                Thread.sleep(2000);                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while landing on the runway.\n", plane.getPlaneName());
                return;
            }   
            System.out.printf("%s has landed and is coasting to the assigned gate.\n", plane.getPlaneName());

        }
         else {
            // another plane is ahead in the queue, release the lock and try again later
            runwayLock.unlock();
        }
    }
    
    private void handleDepart(BlockingQueue<Plane> queue, Plane plane){
        Plane nextPlane = queue.peek();
        //if the plane is the first plane in queue
        if (nextPlane == plane) {
             System.out.printf("%s is departing from the runway...\n", plane.getPlaneName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while departing from the runway.\n", plane.getPlaneName());
                return;
            }
            System.out.printf("%s has departed from the runway.\n", plane.getPlaneName());
        }
         else {
            // another plane is ahead in the queue, release the lock and try again later
            runwayLock.unlock();
        }
    }
    
    public void land(Plane plane) throws InterruptedException {
        enterQueue(plane, landingQueue);
        while (true) {
            runwayLock.lock(); // acquire the runway
            groundSemaphore.acquire(); // acquire the airport ground
            try{
                // Handle emergency landing plane first
                if (!this.emergencyQueue.isEmpty()) {
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
        while (true) {
            runwayLock.lock(); // acquire the lock
            try {
                handleDepart(departingQueue, plane);
                //remove plane from queue
                departingQueue.take();
                //release runway after departing
                runwayLock.unlock();
                groundSemaphore.release();
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
            if (gate1Semaphore.tryAcquire()) {
                System.out.printf("%s is coasting to Gate 1...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 1.\n", plane.getPlaneName());
            } else if (gate2Semaphore.tryAcquire()) {
                System.out.printf("%s is rcoasting to Gate 2...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 2.\n", plane.getPlaneName());
            } else if (gate3Semaphore.tryAcquire()) {
                System.out.printf("%s is rcoasting to Gate 3...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 3.\n", plane.getPlaneName());
            } else {
                System.out.printf("%s is waiting for a gate...\n", plane.getPlaneName());
                occupyGate(plane);
            }
            runwayLock.unlock();
            disembark();
            refillSupplies();
            refuelPlane();
            embark();
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
