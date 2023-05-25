package ro.pub.cs.systems.eim.practicaltest02;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String hour;
    private final String minute;
    private final String operation;
    private final TextView resultTextView;
    private Socket socket;

    public ClientThread(String address, int port,  String hour, String minute, String operation, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.hour = hour;
        this.minute = minute;
        this.operation = operation;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(operation);
            printWriter.flush();
            if(operation.equals(Constants.SET)) {
                printWriter.println(hour);
                printWriter.flush();
                printWriter.println(minute);
                printWriter.flush();
            }

            String result;
            while ((result = bufferedReader.readLine()) != null) {
                final String finalResult = result;

                resultTextView.post(() -> resultTextView.setText(finalResult));
            }
        } catch (IOException e) {
                Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
                    }
                }
            }
        }
}
