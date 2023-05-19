/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccp_assignment;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author User
 */
public class WaitingTime {
    AtomicLong maxWaitingTime = new AtomicLong(0);
    AtomicLong minWaitingTime = new AtomicLong(0);
    AtomicLong totalWaitingTime = new AtomicLong(0);
    AtomicLong waitCount = new AtomicLong(0);
    
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

    public long getWaitCount() {
        return waitCount.longValue();
    }

    public void addWaitCount() {
        waitCount.getAndIncrement();
    }
}
