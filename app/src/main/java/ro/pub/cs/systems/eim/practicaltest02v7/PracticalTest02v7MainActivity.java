package ro.pub.cs.systems.eim.practicaltest02v7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PracticalTest02v7MainActivity extends AppCompatActivity {

    EditText HourEditText;
    EditText MinuteEditText;
    TextView port;
    Button setButton;
    Button resetButton;
    Button pollButton;
    TextView response;
    ServerThread serverThread;

    class SetButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String hour = HourEditText.getText().toString();
            String minute = MinuteEditText.getText().toString();
            if (hour == null || hour.isEmpty() || minute == null || minute.isEmpty()) {
                return;
            }

            ClientAsyncTask clientAsyncTask = new ClientAsyncTask("set", response);
            clientAsyncTask.execute("localhost", port.getText().toString(), hour, minute);
        }
    }

    class ResetButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            ClientAsyncTask clientAsyncTask = new ClientAsyncTask("reset", response);
            clientAsyncTask.execute("localhost", port.getText().toString());
        }
    }

    class PollButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            ClientAsyncTask clientAsyncTask = new ClientAsyncTask("poll", response);
            clientAsyncTask.execute("localhost", port.getText().toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v7_main);
        HourEditText = findViewById(R.id.text_hour);
        MinuteEditText = findViewById(R.id.text_minute);
        port = findViewById(R.id.port);
        setButton = findViewById(R.id.set);
        setButton.setOnClickListener(new SetButtonClickListener());
        resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(new ResetButtonClickListener());
        pollButton = findViewById(R.id.poll);
        pollButton.setOnClickListener(new PollButtonClickListener());
        response = findViewById(R.id.response);

        String serverPort = port.getText().toString();
        serverThread = new ServerThread(Integer.parseInt(serverPort));
        serverThread.startServer();
    }


    @Override
    protected void onDestroy() {
        serverThread.stopServer();
        super.onDestroy();
    }
}