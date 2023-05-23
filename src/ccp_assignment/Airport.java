/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author User
 */
public class Airport{
    private final BlockingQueue<Plane> planes;
    private final BlockingQueue<Plane> landingQueue;
    private final BlockingQueue<Plane> departingQueue;
    private final BlockingQueue<Plane> emergencyQueue;
    private final Semaphore runwaySemaphore = new Semaphore(1);
    private final Semaphore gate1Semaphore = new Semaphore(1);
    private final Semaphore gate2Semaphore = new Semaphore(1);
    private final Semaphore gate3Semaphore = new Semaphore(1);
    private final Semaphore groundSemaphore = new Semaphore(3);
    private RefuelingTruck refuel;
    private int gateNo;
    private int planeOnGround;
    private Clock clock;
    
     public Airport(Clock clock) {
        planes = new LinkedBlockingQueue<>();
        landingQueue = new LinkedBlockingQueue<>();
        departingQueue = new LinkedBlockingQueue<>();
        emergencyQueue = new LinkedBlockingQueue<>();
        refuel = new RefuelingTruck();
        gateNo = 0;
        this.clock = clock;
    }
     
    public int getPlaneOnGround(){
        planeOnGround = 3-groundSemaphore.availablePermits();
        return planeOnGround ;
    }
    
    public void enterPlanesQueue(Plane plane){
        planes.add(plane);
    }
    
    public void getPlanesWaitingTime(){
        while(!planes.isEmpty()){
            Plane plane = planes.peek();
            System.out.println("Maximum waiting time of "+plane.getPlaneName()+": "+plane.getMaxWaitingTime()+"ms");
            System.out.println("Minimum waiting time of "+plane.getPlaneName()+": "+plane.getMinWaitingTime()+"ms");
            System.out.println("Average waiting time of "+plane.getPlaneName()+": "+plane.getAverageWaitingTime()+"ms");
            System.out.println();
            try{
                planes.take();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        
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
            System.out.printf("%s has landed on the runway. \n", plane.getPlaneName());

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
        
        if(plane.getFuelLevel() < plane.getMIN_FUEL_LEVEL()){
            System.out.printf("EMERGENCY LANDING REQUIRED: %s is getting permission to land on the runway...\n", plane.getPlaneName());
        }else{
            System.out.printf("%s is getting permission to land on the runway...\n", plane.getPlaneName());
        }
        
        long startTime = System.currentTimeMillis();
        while (true) {
            //ensure that both ground and runway semaphore can be acquired  
            if(runwaySemaphore.tryAcquire()){
                if(groundSemaphore.tryAcquire()){
                    // permission obtained, stopwatch stop
                    long endTime = System.currentTimeMillis();
                    long waitingTime = endTime - startTime;
                    plane.updateWaitingTime(waitingTime);
                    try{
                        // Handle emergency landing plane first
                        if (!emergencyQueue.isEmpty()&& emergencyQueue.peek()== plane) {
                            handleLand(emergencyQueue, plane);
                            emergencyQueue.take();
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
                }else{
                    //release groudSemaphore if cant acquire runway semaphore
                    runwaySemaphore.release();
                }
            }    
        }
    }

    public void depart(Plane plane) throws InterruptedException {
        System.out.printf("%s is getting permission to coast to runway to depart...\n", plane.getPlaneName());
        long startTime = System.currentTimeMillis();
        while (true) {
           if((!this.emergencyQueue.isEmpty() && planeOnGround == 3) || this.emergencyQueue.isEmpty()){
                runwaySemaphore.acquire(); // acquire the lock
                // permission obtained, stopwatch stop
                long endTime = System.currentTimeMillis();
                long waitingTime = endTime - startTime;
                plane.updateWaitingTime(waitingTime);
                try {
                    System.out.printf("%s is coasting to the runway to depart...\n", plane.getPlaneName());
                    Thread.sleep(1000);
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
                    int fuelUsage = rand.nextInt(80);
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
            long startTime = System.currentTimeMillis();
            if (gate1Semaphore.tryAcquire()) {
                // permission obtained, stopwatch stop
                long endTime = System.currentTimeMillis();
                long waitingTime = endTime - startTime;
                plane.updateWaitingTime(waitingTime);
                System.out.printf("%s is coasting to Gate 1...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 1.\n", plane.getPlaneName());
                gateNo = 1;
            } else if (gate2Semaphore.tryAcquire()) {
                // permission obtained, stopwatch stop
                long endTime = System.currentTimeMillis();
                long waitingTime = endTime - startTime;
                plane.updateWaitingTime(waitingTime);
                System.out.printf("%s is coasting to Gate 2...\n", plane.getPlaneName());
                Thread.sleep(1000); // simulate refueling time
                System.out.printf("%s has coasted and docked at Gate 2.\n", plane.getPlaneName());
                gateNo = 2;
            } else if (gate3Semaphore.tryAcquire()) {
                // permission obtained, stopwatch stop
                long endTime = System.currentTimeMillis();
                long waitingTime = endTime - startTime;
                plane.updateWaitingTime(waitingTime);
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
