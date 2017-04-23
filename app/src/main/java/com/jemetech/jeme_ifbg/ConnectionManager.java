package com.jemetech.jeme_ifbg;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Lei on 11/27/2016.
 */

public class ConnectionManager {
    private static ConnectionManager instance = new ConnectionManager();
    public static ConnectionManager getInstance() {
        return instance;
    }
    private Connection connection;
    private ConnectionManager(){}
    public Connection getConnection() {
        return connection;
    }

    public void connect(final String theHost, final int port, final ConnectionListerner listener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(theHost, port);
                    connection = new Connection();
                    connection.listen(socket);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listener.onSuccess();
            }
        }.start();
    }
}
