/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author User
 */
 
 class Plane extends Thread{
    private String planeName;
    private int fuelLevel;
    private static final int MIN_FUEL_LEVEL= 30;
    private static final int MAX_FUEL_LEVEL = 100;
    private int passengerNum;
    private Counter counter;
    private static int planeNum = 0;
    private Airport airport;
    private WaitingTime waitingTime;
    public Boolean closingTime;
    private Clock clock;
    private long maxWaitingTime;
    private long minWaitingTime;
    private long averageWaitingTime;
    private int gateNo;
    private Random rand;
    private boolean serviceEnd;
    
    public Plane(String planeName, Counter counter, Airport airport, Clock clock){
        rand = new Random();
        this.planeName = planeName;
        fuelLevel = MAX_FUEL_LEVEL;
        this.counter = counter;
        planeNum++;
        this.airport = airport; // shared airport
        this.clock = clock;
        waitingTime = new WaitingTime();
        airport.enterPlanesQueue(this);
    }

    public boolean getServiceEnd(){
        return serviceEnd;
    }
    
    public void setServiceEnd(boolean flag){
        serviceEnd  = flag;
    }
    
    public int getGateNo() {
        return gateNo;
    }

    public void setGateNo(int gateNo) {
        this.gateNo = gateNo;
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

    public long getMaxWaitingTime() {
        maxWaitingTime = waitingTime.getMaxWaitingTime();
        return maxWaitingTime;
    }

    public long getMinWaitingTime() {
        minWaitingTime = waitingTime.getMinWaitingTime();
        return minWaitingTime;
    }

    public long getAverageWaitingTime() {
        averageWaitingTime = waitingTime.getAverageWaitingTime();
        return averageWaitingTime;
    }
    
    public void updateWaitingTime(long time){
        // Update max and min waiting time
        if(waitingTime.getWaitCount() == 0){
            waitingTime.setMaxWaitingTime(time);
            waitingTime.setMinWaitingTime(time);
        }
        else if (time > waitingTime.getMaxWaitingTime()){
            waitingTime.setMaxWaitingTime(time);
        }else if (time < waitingTime.getMinWaitingTime()){
            waitingTime.setMinWaitingTime(time);
        }
        waitingTime.addTotalWaitingTime(time);
        waitingTime.addWaitCount();
    }
    
    // disembark passengers fetched from other airports
    public synchronized void disembark() {
        passengerNum = rand.nextInt(50);
        System.out.printf("%s is disembarking " +passengerNum+ " passengers at Gate "+gateNo+"...\n", planeName);

        for (int i = 0; i < passengerNum ; i++) {
            try {
                Thread.sleep(200);
                System.out.println(planeName+ ": Passenger " + (i+1) + " has disembarked at Gate "+gateNo+".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while disembarking passengers at Gate "+gateNo+".\n", planeName);
                return;
            }
        }   
        counter.add(passengerNum);
        System.out.println(planeName+ ": All " +passengerNum+ " passengers have disembarked at Gate "+gateNo+".");
    }

    //embark new passengers
    public synchronized void embark() {
        passengerNum = rand.nextInt(50);
        int j = 1;
        System.out.printf("%s is embarking " +passengerNum+ " passengers at Gate "+gateNo+"...\n", planeName);
        for (int i = passengerNum; i > 0 ; i--) {
            try {
                Thread.sleep(200);
                System.out.println(planeName+ ": Passenger " + j++ + " has embarked at Gate "+gateNo+".");
               
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s was interrupted while embarking passengers at Gate "+gateNo+".\n", planeName);
                return;
            }
        }
        counter.add(passengerNum);
        System.out.println(planeName+ ": All " +passengerNum+ " passengers have embarked at Gate "+gateNo+".");
    }
    
    public void run() {
        while(!clock.closingTime){
            try{
                airport.land(this);
                airport.occupyGate(this);
                airport.depart(this);
                TimeUnit.SECONDS.sleep(rand.nextInt(4));
            }catch(InterruptedException e){
                e.printStackTrace();;
            }
        }
    }
    }

