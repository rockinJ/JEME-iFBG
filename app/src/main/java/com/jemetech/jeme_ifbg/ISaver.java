package com.jemetech.jeme_ifbg;

/**
 * Created by dbhr_ on 2017/5/14.
 */

public interface ISaver {
    void save(Sensor.Data aData);
    void save();
    boolean removing();
    void close();
}
