package com.jemetech.jeme_ifbg;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lei on 11/29/2016.
 */

public class Sensor {

    private static final int size = 400;
    private final String name;
    private final int id;
    private Deque<Data> data = new LinkedList<>();
    private boolean full;
    private Lock lock = new ReentrantLock();
    private Data last;
    private boolean debug = false;
    public float max;
    public float min;
    private boolean maxSet;
    private boolean minSet;

    public Sensor(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Sensor(int id) {
        this("Sensor", id);
    }

    @Override
    public String toString() {
        if(last == null)
            return (name+" "+id).intern();
        else
            return name+" "+id+" "+last.value;
    }

    public boolean ready() {
        return maxSet && minSet;
    }

    public void push(Data d) {
        last = d;
        if(!maxSet) {
            maxSet = true;
            max = d.value;
        } else if (d.value > max)
            max = d.value;
        if(!minSet) {
            minSet = true;
            min = d.value;
        } else if (d.value < min)
            min = d.value;
        lock.lock();
        data.add(d);
        if (full)
            data.poll();
        else if (data.size() == size)
            full = true;
        lock.unlock();
        if(debug)
            System.out.println("id: "+id+", time: "+d.time+", value: "+d.value);
    }

    public void debug(boolean d) {
        debug = d;
    }

    public Iterator<Data> getData() {
        lock.lock();
        Deque<Data> data = (Deque) ((LinkedList)this.data).clone();
        lock.unlock();
        return data.descendingIterator();
    }

    public static class Data {
        public long time;
        public float value;
        public Data (long time, float value) {
            this.time = time;
            this.value = value;
        }
    }
}
