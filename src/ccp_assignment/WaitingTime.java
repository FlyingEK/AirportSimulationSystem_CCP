/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author User
 */
public class WaitingTime {
    AtomicLong maxWaitingTime = new AtomicLong();
    AtomicLong minWaitingTime = new AtomicLong(0);
    AtomicLong totalWaitingTime = new AtomicLong(0);
    AtomicInteger waitCount = new AtomicInteger(0);
    
   public long getMaxWaitingTime() {
        return maxWaitingTime.longValue();
    }

    public void setMaxWaitingTime(long maxWaitingTime) {
        this.maxWaitingTime.set(maxWaitingTime);
    }

    public long getMinWaitingTime() {
        return minWaitingTime.longValue();
    }

    public void setMinWaitingTime(long minWaitingTime) {
        this.minWaitingTime.set(minWaitingTime);
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime.longValue();
    }

    public void addTotalWaitingTime(long waitingTime) {
        this.totalWaitingTime.getAndAdd(waitingTime);
    }

    public int getWaitCount() {
        return waitCount.intValue();
    }

    public void addWaitCount() {
        waitCount.getAndIncrement();
    }
    
    public long getAverageWaitingTime(){
        long average = 0;
        try{
            average = getTotalWaitingTime()/getWaitCount();
        }catch(Exception e){
            e.printStackTrace();
        }
        return average;
    }
}
