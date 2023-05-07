/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ccp_assignment;
import java.util.Random;
/**
 *
 * @author User
 */
public class CCP_Assignment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // A plane thread start every 1-3 seconds
        PassengerCounter c = new PassengerCounter();
        Plane plane1 = new Plane("Plane 1", c);
        Plane plane2 = new Plane("Plane 2", c);

        plane1.start();
        plane2.start();

        try {
    plane1.join(); // wait for plane1 thread to finish
    plane2.join(); // wait for plane2 thread to finish
} catch (InterruptedException e) {
    e.printStackTrace();
}

        System.out.print("" + c.getTotalPassenger());
}}
