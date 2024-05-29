package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {
    private Socket socket;
    private ServerThread serverThread;
    private int hour;
    private int minute;

    public CommunicationThread(Socket socket, ServerThread serverThread) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    public void run() {
        if (socket == null) {
            Log.d("PracticalTest02", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        Log.d("PracticalTest02", "[COMMUNICATION THREAD] Started!");

        try {
            BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            String command = bufferedReader.readLine();
            if (command.equals("set")) {
                Log.d("PracticalTest02", "[COMMUNICATION THREAD] Set command received!");
                String hourString = bufferedReader.readLine();
                String minuteString = bufferedReader.readLine();
                if (hourString == null || hourString.isEmpty() || minuteString == null || minuteString.isEmpty()) {
                    Log.d("PracticalTest02", "[COMMUNICATION THREAD] Error receiving parameters from client (hour / minute)!");
                    return;
                }
                hour = Integer.parseInt(hourString);
                minute = Integer.parseInt(minuteString);
                AlarmInformation alarmInformation = new AlarmInformation(hour, minute);
                Log.d("PracticalTest02", "[COMMUNICATION THREAD] Alarm set for " + hour + ":" + minute + "!" + socket.getInetAddress().toString());
                serverThread.data.put(socket.getInetAddress().toString(), alarmInformation);
            } else if (command.equals("reset")) {
                Log.d("PracticalTest02", "[COMMUNICATION THREAD] Reset command received!");
                serverThread.data.remove(socket.getInetAddress().toString());
            } else if (command.equals("poll")) {
                String response = "";
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                Log.d("PracticalTest02", "[COMMUNICATION THREAD] Poll command received!");
                AlarmInformation alarmInformation = serverThread.data.get(socket.getInetAddress().toString());
                if (alarmInformation == null) {
                    Log.d("PracticalTest02", "[COMMUNICATION THREAD] No alarm set for this client!");
                    printWriter.println("none");
                    return;
                }
                Log.d("PracticalTest02", "[COMMUNICATION THREAD] Alarm set for " + alarmInformation.hour + ":" + alarmInformation.minute + "!");

                Socket socketUTC = new Socket("utcnist.colorado.edu", 13);
                BufferedReader bufferedReaderUTC = new BufferedReader(new java.io.InputStreamReader(socketUTC.getInputStream()));
                String line;
                while ((line = bufferedReaderUTC.readLine()) != null) {
                    if (line.contains("UTC")) {
                        String[] tokens = line.split(" ");
                        String[] timeTokens = tokens[2].split(":");
                        int hourUTC = Integer.parseInt(timeTokens[0]);
                        int minuteUTC = Integer.parseInt(timeTokens[1]);
                        Log.d("PracticalTest02", "[COMMUNICATION THREAD] UTC time: " + hourUTC + ":" + minuteUTC);
                        if (hourUTC < alarmInformation.hour)
                            response = "inactive";
                        else if (hourUTC == alarmInformation.hour && minuteUTC < alarmInformation.minute)
                            response = "inactive";
                        else
                            response = "active";

                        break;
                    }
                }
                socketUTC.close();
                printWriter.println(response);
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
