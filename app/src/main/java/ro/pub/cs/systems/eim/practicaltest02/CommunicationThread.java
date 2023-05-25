package ro.pub.cs.systems.eim.practicaltest02;

import android.net.SocketKeepalive;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    private final Socket socket;
    private  Socket socketTimeStamp;
    private final ServerThread serverThread;


    public CommunicationThread(Socket socket, ServerThread serverThread){
        this.socket = socket;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");

            String operation = bufferedReader.readLine();
            if (operation == null || operation.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (pokemon name)!");
                return;
            }

            switch (operation) {
                case Constants.SET:
                    String hour = bufferedReader.readLine();
                    String minute = bufferedReader.readLine();
                    serverThread.getAlarms().put(socket.getInetAddress().toString(), hour + ":" + minute);
                    printWriter.println("Alarm set for " + hour + ":" + minute);
                    printWriter.flush();
                    break;
                case Constants.RESET:
                    printWriter.println("Alarm reset for " + serverThread.getAlarms().get(socket.getInetAddress().toString()));
                    serverThread.getAlarms().remove(socket.getInetAddress().toString());
                    printWriter.flush();
                    break;
                case Constants.POLL:
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                    socketTimeStamp = new Socket("utcnist.colorado.edu", 13);
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Connected to the webservice!");
                    BufferedReader bufferedReaderTimeStamp = Utilities.getReader(socketTimeStamp);
                    String timestamp = null;
                    while ((timestamp = bufferedReaderTimeStamp.readLine()) != null) {
                        timestamp = bufferedReaderTimeStamp.readLine();
                        String[] time = timestamp.split(" ")[2].split(":");
                        String hourTimeStamp = time[0];
                        String minuteTimeStamp = time[1];
                        printWriter.println(hourTimeStamp + ":" + minuteTimeStamp);
                        if(!serverThread.getAlarms().containsKey(socket.getInetAddress().toString())) {
                            printWriter.println("none\n");
                            printWriter.flush();
                        } else {
                            String time2 = serverThread.getAlarms().get(socket.getInetAddress().toString());
                            String hour2 = time2.split(":")[0];
                            String minute2 = time2.split(":")[1];
                            if(Integer.parseInt(hourTimeStamp) > Integer.parseInt(hour2) || (Integer.parseInt(hourTimeStamp) == Integer.parseInt(hour2) && Integer.parseInt(minuteTimeStamp) > Integer.parseInt(minute2)))
                                printWriter.println("active\n");
                            else
                                printWriter.println("inactive\n");
                            printWriter.flush();
                        }
                    }
                    break;
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            try {
                socket.close();
                if(socketTimeStamp != null)
                    socketTimeStamp.close();
            } catch (IOException e) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
            }
        }
    }
}
