/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author User
 */
public class RefuelingTruck {
    private Lock refuelingLock = new ReentrantLock();

    public void refuelPlane(Plane plane) {
        System.out.printf("%s is getting permission to the refueling truck...\n", plane.getPlaneName());
        long startTime = System.currentTimeMillis();
        while(true){
            try {
                refuelingLock.lock();
                // permission obtained, stopwatch stop
                long endTime = System.currentTimeMillis();
                long waitingTime = endTime - startTime;
                plane.updateWaitingTime(waitingTime);
                System.out.printf("%s is refueling...\n", plane.getPlaneName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.printf("%s was interrupted while refueling\n", plane.getPlaneName());
                    return;
                }
                plane.setFuelLevel(plane.getMAX_FUEL_LEVEL());
                System.out.printf("%s has fully refueled.\n", plane.getPlaneName());
                return;
            } finally {
                refuelingLock.unlock();
            }
        }
    }
    
}
