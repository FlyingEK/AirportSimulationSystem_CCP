/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;
import java.util.List;
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
    private final BlockingQueue<Plane> planes;
    private final BlockingQueue<Plane> landingQueue;
    private final BlockingQueue<Plane> departingQueue;
    private final BlockingQueue<Plane> emergencyQueue;
    private final Semaphore runwaySemaphore = new Semaphore(1);
    private final Semaphore gate1Semaphore = new Semaphore(1);
    private final Semaphore gate2Semaphore = new Semaphore(1);
    private final Semaphore gate3Semaphore = new Semaphore(1);
    private final Semaphore groundSemaphore = new Semaphore(3);
    private PlaneService service;
    private int planeNoGate1;
    private int planeNoGate2;
    private int planeNoGate3;
    private int planeOnGround;
    private long landStartTime;
    private long departStartTime;
    private int totalPlane;
    private RefuelingTruck truck;
    private Clock clock;
     public Airport(Clock clock, RefuelingTruck truck) {
        planes = new LinkedBlockingQueue<>();
        landingQueue = new LinkedBlockingQueue<>();
        departingQueue = new LinkedBlockingQueue<>();
        emergencyQueue = new LinkedBlockingQueue<>();    
        this.truck = truck;
        this.clock = clock;
    }
    
    public int getPlaneOnGround(){
        planeOnGround = 3-groundSemaphore.availablePermits();
        return planeOnGround ;
    }

    public int getPlaneNoGate1() {
        planeNoGate1 = 1 - gate1Semaphore.availablePermits();
        return planeNoGate1;
    }

    public int getPlaneNoGate2() {
        planeNoGate2 = 1 - gate2Semaphore.availablePermits();
        return planeNoGate2;
    }

    public int getPlaneNoGate3() {
        planeNoGate3 = 1 - gate3Semaphore.availablePermits();
        return planeNoGate3;
    }

    public int getTotalPlane() {
        totalPlane = planes.size();
        return totalPlane;
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
    
    private void enterQueue( Plane plane){
        // if fuel level is low, plane enters emergency landing queue
        if(plane.getFuelLevel() < plane.getMIN_FUEL_LEVEL()){
            try{
                emergencyQueue.put(plane);
                System.out.printf("EMERGENCY LANDING REQUIRED: %s is getting permission to land on the runway...\n", plane.getPlaneName());
                landStartTime = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try{
                landingQueue.put(plane);
                System.out.printf("%s is getting permission to land on the runway...\n", plane.getPlaneName());
                landStartTime = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public void land(Plane plane){
         enterQueue(plane);
         handleLand(plane);        
    }
    
    private void handleLand(Plane plane){
        //handle emergency plane first
        while(!emergencyQueue.isEmpty()){
            if(!emergencyQueue.contains(plane)){
                try {
                    Thread.sleep(2000);                
                } catch (InterruptedException e) {
                       e.printStackTrace();
                    return;
                }
                continue;
            }else{
                //loop until the plane is the first plane in queue
                while(true){
                    if(emergencyQueue.peek()== plane){
                    //loop until runway and ground is available
                        while (true) {
                            //ensure that both ground and runway semaphore can be acquired  
                            if(runwaySemaphore.tryAcquire()){
                                if(groundSemaphore.tryAcquire()){
                                    // permission obtained, stopwatch stop
                                    long endTime = System.currentTimeMillis();
                                    long waitingTime = endTime - landStartTime;
                                    plane.updateWaitingTime(waitingTime);
                                     System.out.printf("%s is landing on the runway...\n", plane.getPlaneName());
                                    try {
                                        Thread.sleep(2000);                
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        System.out.printf("%s was interrupted while landing on the runway.\n", plane.getPlaneName());
                                        return;
                                    }
                                    System.out.printf("%s has landed on the runway. \n", plane.getPlaneName());
                                    try {
                                        emergencyQueue.take();
                                        return;
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }else{
                                    //release groudSemaphore if cant acquire runway semaphore
                                    runwaySemaphore.release();
                                }
                            }    
                        }  
                    }
                }
            }      
        }
        // Handle other landing plane
        while(true){
             if(landingQueue.peek()==plane){
                //here {
                while (true) {
                    // during the permission requesting, check again if there is any emergency plane
                    if(!emergencyQueue.isEmpty()){
                        handleLand(plane);
                        return;
                    }
                    //ensure that both ground and runway semaphore can be acquired  
                    if(runwaySemaphore.tryAcquire()){
                        if(groundSemaphore.tryAcquire()){
                            // permission obtained, stopwatch stop
                            long endTime = System.currentTimeMillis();
                            long waitingTime = endTime - landStartTime;
                            plane.updateWaitingTime(waitingTime);
                            System.out.printf("%s is landing on the runway...\n", plane.getPlaneName());
                            try {
                                Thread.sleep(2000);                
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.printf("%s was interrupted while landing on the runway.\n", plane.getPlaneName());
                                return;
                            }
                            System.out.printf("%s has landed on the runway. \n", plane.getPlaneName());
                            try {
                                landingQueue.take();
                                return;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }

                        }else{
                            //release runwaySemaphore if cant acquire runway semaphore
                            runwaySemaphore.release();
                        }
                    }    
                }  
            }
        }
    }
    
    
    private void handleDepart(Plane plane){
        //loop until the plane is the first plane in queue
        while(true){
            if (plane == departingQueue.peek()){
                try{
                 runwaySemaphore.acquire(); // acquire the lock
                }catch(Exception e){
                    e.printStackTrace();
                }
                 // permission obtained, stopwatch stop
                 long endTime = System.currentTimeMillis();
                 long waitingTime = endTime - departStartTime;
                 plane.updateWaitingTime(waitingTime);
                 try {
                     System.out.printf("%s is coasting to the runway to depart...\n", plane.getPlaneName());
                     Thread.sleep(1000);
                     System.out.printf("%s has coasted to the runway to depart.\n", plane.getPlaneName());
                     //release gate semaphore
                     if(plane.getGateNo()== 1){
                         gate1Semaphore.release();
                     }else if(plane.getGateNo() ==2){
                         gate2Semaphore.release(); 
                     }else if (plane.getGateNo() ==3){
                         gate3Semaphore.release();
                     }
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                     System.out.printf("%s was interrupted while coasting to the runway.\n", plane.getPlaneName());
                     return;
                 } 

                System.out.printf("%s is departing from the runway...\n", plane.getPlaneName());
                 try {
                     Thread.sleep(2000);                        
                     System.out.printf("%s has departed from the runway.\n", plane.getPlaneName());
                     //release runway after departing
                     runwaySemaphore.release();
                     groundSemaphore.release();
                     //remove plane from queue  
                     departingQueue.take();
                     return;
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                     System.out.printf("%s was interrupted while departing from the runway.\n", plane.getPlaneName());
                     return;
                 } 
            }
        }
    }
    
     public void depart(Plane plane) throws InterruptedException {
        while(true){
            // check if cleaning, refill is done
            if(plane.getServiceEnd()){
                departingQueue.add(plane);
                System.out.printf("%s is getting permission to coast to runway to depart...\n", plane.getPlaneName());
                departStartTime = System.currentTimeMillis();
                handleDepart(plane);

                //simulate the plane travel by reducing fuel level
                Random rand = new Random();
                // set min and max range for random rand.nextInt(max - min + 1) + min
                //min 30 - max 80
                int fuelUsage = rand.nextInt(80-30+1)+30;
                int originalFuelLevel = plane.getFuelLevel();
                plane.setFuelLevel(originalFuelLevel - fuelUsage);
                return;
            }
        }
    }
   

    public void occupyGate(Plane plane) {
        try{
            System.out.printf("%s is getting permission to coast to the gate...\n", plane.getPlaneName());
            long startTime = System.currentTimeMillis();
            while(true){
                if (gate1Semaphore.tryAcquire()) {
                    // permission obtained, stopwatch stop
                    long endTime = System.currentTimeMillis();
                    long waitingTime = endTime - startTime;
                    plane.updateWaitingTime(waitingTime);
                    System.out.printf("%s is coasting to Gate 1...\n", plane.getPlaneName());
                    Thread.sleep(1000); 
                    System.out.printf("%s has coasted and docked at Gate 1.\n", plane.getPlaneName());
                    plane.setGateNo(1);
                    break;
                } else if (gate2Semaphore.tryAcquire()) {
                    // permission obtained, stopwatch stop
                    long endTime = System.currentTimeMillis();
                    long waitingTime = endTime - startTime;
                    plane.updateWaitingTime(waitingTime);
                    System.out.printf("%s is coasting to Gate 2...\n", plane.getPlaneName());
                    Thread.sleep(1000); 
                    System.out.printf("%s has coasted and docked at Gate 2.\n", plane.getPlaneName());
                    plane.setGateNo(2);
                    break;
                } else if (gate3Semaphore.tryAcquire()) {
                    // permission obtained, stopwatch stop
                    long endTime = System.currentTimeMillis();
                    long waitingTime = endTime - startTime;
                    plane.updateWaitingTime(waitingTime);
                    System.out.printf("%s is coasting to Gate 3...\n", plane.getPlaneName());
                    Thread.sleep(1000); 
                    System.out.printf("%s has coasted and docked at Gate 3.\n", plane.getPlaneName());
                    plane.setGateNo(3);
                    break;
                } else {
                    System.out.printf("%s is waiting for a gate...\n", plane.getPlaneName());
                    //continue
                    continue;
                }
            }
            runwaySemaphore.release();
            service = new PlaneService(plane);
            service.start();      
            truck.refuelPlane(plane);
            plane.disembark();
            plane.embark();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s was interrupted while coasting to the gate.\n", plane.getPlaneName());
            return;
        }
    }
  
}
