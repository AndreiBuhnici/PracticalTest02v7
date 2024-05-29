package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    private boolean isRunning;
    private ServerSocket serverSocket;
    private int port;
    public HashMap<String, AlarmInformation> data;


    public ServerThread(int port) {
        this.port = port;
    }

    public void startServer() {
        isRunning = true;
        data = new HashMap<>();
        start();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Log.d("PracticalTest02", "[SERVER THREAD] Waiting for a client request...");
            while (isRunning) {
                Socket socket = serverSocket.accept();
                Log.d("PracticalTest02", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                if (socket != null) {
                    CommunicationThread communicationThread = new CommunicationThread(socket, this);
                    communicationThread.start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
