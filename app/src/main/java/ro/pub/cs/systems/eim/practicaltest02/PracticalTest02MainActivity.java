package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;

    private ServerThread serverThread = null;

    private class ConnectButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, serverPort);
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }
    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private EditText serverAddressEditText = null;

    private EditText clientServerPortEditText = null;

    private EditText hourEditText = null;
    private EditText minuteEditText = null;
    private TextView resultText = null;

    private class SetButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverAddress = serverAddressEditText.getText().toString();
            String serverPort = clientServerPortEditText.getText().toString();
            if (serverAddress.isEmpty() || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server address and port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            String hour = hourEditText.getText().toString();
            String minute = minuteEditText.getText().toString();
            if (hour.isEmpty() || minute.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] hour and minute fields should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, "[MAIN ACTIVITY] " + serverAddress + " " + serverPort + " " + hour);

            ClientThread clientThread = new ClientThread(
                    serverAddress,
                    Integer.parseInt(serverPort),
                    hour,
                    minute,
                    Constants.SET, resultText);
            clientThread.start();
        }
    }

    private final SetButtonClickListener setButtonClickListener = new SetButtonClickListener();

    private class ResetButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverAddress = serverAddressEditText.getText().toString();
            String serverPort = clientServerPortEditText.getText().toString();
            if (serverAddress.isEmpty() || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server address and port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, "[MAIN ACTIVITY] " + serverAddress + " " + serverPort);

            ClientThread clientThread = new ClientThread(
                    serverAddress,
                    Integer.parseInt(serverPort),
                    "",
                    "",
                    Constants.RESET, resultText);
            clientThread.start();
        }
    }

    private final ResetButtonClickListener resetButtonClickListener = new ResetButtonClickListener();

    private class PollButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverAddress = serverAddressEditText.getText().toString();
            String serverPort = clientServerPortEditText.getText().toString();
            if (serverAddress.isEmpty() || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server address and port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, "[MAIN ACTIVITY] " + serverAddress + " " + serverPort);

            ClientThread clientThread = new ClientThread(
                    serverAddress,
                    Integer.parseInt(serverPort),
                    "", "", Constants.POLL, resultText);
            clientThread.start();
        }
    }

    private final PollButtonClickListener pollButtonClickListener = new PollButtonClickListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method was invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        Button connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);
        serverAddressEditText = (EditText)findViewById(R.id.server_address_edit_text);
        clientServerPortEditText = (EditText)findViewById(R.id.client_server_port_edit_text);
        hourEditText = (EditText)findViewById(R.id.hour_edit_text);
        minuteEditText = (EditText)findViewById(R.id.minute_edit_text);
        Button setButton = (Button)findViewById(R.id.set_button);
        setButton.setOnClickListener(setButtonClickListener);
        Button resetButton = (Button)findViewById(R.id.reset_button);
        resetButton.setOnClickListener(resetButtonClickListener);
        Button pollButton = (Button)findViewById(R.id.poll_button);
        pollButton.setOnClickListener(pollButtonClickListener);
        resultText = (TextView)findViewById(R.id.result);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method was invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}