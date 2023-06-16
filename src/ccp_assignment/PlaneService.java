/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;

/**
 *
 * @author User
 */
public class PlaneService extends Thread{
    private Plane plane;
    boolean serviceEnd;
    PlaneService(Plane plane){
        this.plane = plane;
    }
    
    private void refillSupplies(Plane plane){
        System.out.printf("%s: Staff is refilling the supplies...\n", plane.getPlaneName());
        try {
            Thread.sleep(1000);                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s: Staff was interrupted while refilling supplies.\n", plane.getPlaneName());
            return;
        }  
    }
    
    private void cleaning(Plane plane){
        System.out.printf("%s: Staff performing aircraft cleaning...\n", plane.getPlaneName());
        try {
            Thread.sleep(1000);                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("%s: Staff was interrupted while performing aircraft cleaning.\n", plane.getPlaneName());
            return;
        }  
    }
    
    public void run() {
        plane.setServiceEnd(false);
        refillSupplies(plane);
        cleaning(plane);
        //service end
        plane.setServiceEnd(true);
    }
}
