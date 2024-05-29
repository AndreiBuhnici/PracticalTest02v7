package ro.pub.cs.systems.eim.practicaltest02v7;

import android.os.AsyncTask;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAsyncTask extends AsyncTask<String, String, Void> {

    private String command;
    private String response;
    TextView resposeTextView;

    public ClientAsyncTask(String command, TextView responseTextView) {
        this.command = command;
        this.resposeTextView = responseTextView;
    }


    @Override
    protected Void doInBackground(String... strings) {
        String serverAddress = strings[0];
        int serverPort = Integer.parseInt(strings[1]);
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(command);
            if (command.equals("set")) {
                int hour = Integer.parseInt(strings[2]);
                int minute = Integer.parseInt(strings[3]);
                printWriter.println(hour);
                printWriter.println(minute);
            } else if (command.equals("poll")) {
                BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
                response = bufferedReader.readLine();
            }

            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        if (command.equals("poll")) {
            resposeTextView.setText(response);
        }
    }
}
