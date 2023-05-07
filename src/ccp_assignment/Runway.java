/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;
import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
/**
 *
 * @author User
 */
public class Runway {
     private Semaphore runwaySemaphore;
     BlockingQueue<Plane> landingQueue = new LinkedBlockingQueue<>();
     
      public Runway() {
        this.runwaySemaphore = new Semaphore(1);
    }

    // Method to add a plane to the landing queue
    public void addPlaneToLandingQueue(Plane plane) {
        try {
            landingLock.lock();
            landingQueue.put(plane);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while adding plane to landing queue");
        } finally {
            landingLock.unlock();
        }
    }
    
    public void land(Plane plane) throws InterruptedException {
        System.out.printf("%s is waiting for permission to land.\n", plane.getPlaneName());
        runwaySemaphore.acquire();
        System.out.printf("%s has permission to land on the runway.\n", plane.getPlaneName());
        Thread.sleep(1000);
        System.out.printf("%s has landed on the runway.\n", plane.getPlaneName());
    }

    public void depart(Plane plane) throws InterruptedException {
        System.out.printf("%s is waiting for permission to take off.\n", plane.getPlaneName());
        runwaySemaphore.acquire();
        System.out.printf("%s has permission to take off on the runway.\n", plane.getPlaneName());
        Thread.sleep(1000);
        System.out.printf("%s has taken off from the runway.\n", plane.getPlaneName());
        runwaySemaphore.release();
    }
}
