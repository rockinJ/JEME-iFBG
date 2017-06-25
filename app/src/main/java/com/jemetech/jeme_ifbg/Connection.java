package com.jemetech.jeme_ifbg;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Lei on 11/27/2016.
 */

public class Connection {

    private final Sensor.Converter<?> converter;
    private Sensor[] sensors;
    private Socket socket;
    private byte[] header = new byte[4];
    private IntBuffer headBuffer;
    private Collection<UpdateListener> listeners = new HashSet<>();
    private Object lock = new Object();
    byte[] data = null;
    private AtomicBoolean closed = new AtomicBoolean(false);
    private ISaver saver;
    private ISaver pendingSaver;

    public Connection(Sensor.Converter<?> converter) {
        this.converter = converter;
        ByteBuffer buffer = ByteBuffer.wrap(header);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        headBuffer = buffer.asIntBuffer();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if(closed.get())
                        return;
                    synchronized (lock) {
                        try {
                            lock.wait();
                            update();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    public Sensor[] getSensors() {
        return sensors;
    }

    public void listen(final Socket socket) throws IOException {
        this.socket = socket;
        final InputStream in = socket.getInputStream();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if(closed.get())
                        return;
                    try {
                        in.read(header);
                        headBuffer.rewind();
                        int length = headBuffer.get();
                        if(data == null)
                            data = new byte[length];
                        in.read(data);
                        ByteBuffer buffer = ByteBuffer.wrap(data);
                        buffer.order(ByteOrder.LITTLE_ENDIAN);
                        short sc = buffer.getShort();
                        if (sensors == null)
                            sensors = new Sensor[sc];
                        if (pendingSaver != null) {
                            saver = pendingSaver;
                            pendingSaver = null;
                        }
                        for (int i = 0; i < sensors.length; i++) {
                            short size = buffer.getShort();
                            short id = buffer.getShort();
                            int freq = buffer.getInt();
                            if (sensors[i] == null) {
                                sensors[i] = new Sensor(id);
                                sensors[i].setConverter(converter);
                            }
                            short year = buffer.getShort();
                            short month = buffer.get();
                            short day = buffer.get();
                            short hour = buffer.get();
                            short minute = buffer.get();
                            int second = buffer.getShort() & 0xffff;
                            float value = buffer.getFloat();
                            Sensor.Data aData = new Sensor.Data(year, month, day, hour, minute, second, value);
                            sensors[i].push(aData);
                            if(saver != null)
                                saver.save(aData);
                        }
                        if(saver != null) {
                            saver.save();
                            if (saver.removing()) {
                                saver.close();
                                saver = null;
                            }
                        }
                        if(length > data.length)
                            in.skip(length-data.length);
                        synchronized (lock) {
                            lock.notify();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }.start();
    }

    public void setSaver(ISaver saver) {
        pendingSaver = saver;
    }

    private void update() {
        for (UpdateListener l : listeners) {
            l.update();
        }
    }

    public void addUpdateListener(UpdateListener l) {
        listeners.add(l);
    }

    public void close() {
        closed.set(true);
        if(socket != null) {
            try{
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static interface UpdateListener {
        void update();
    }
}
