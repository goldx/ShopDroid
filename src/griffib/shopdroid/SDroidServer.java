package griffib.shopdroid;

import java.io.*;
import java.net.*;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SDroidServer extends Service {

  /**
   * 
   */
  @Override
  public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  /**
   * 
   */
  @Override
  public boolean stopService(Intent name) {
    stopServer();
    return super.stopService(name);
  }
  
  @Override
  public void onStart(Intent intent, int startId) {
    startServer();
    super.onStart(intent, startId);
  }
  
  /**
   * 
   */
  private void startServer() {
    // Start a new thread - we don't want to hang the UI
    new Thread(new Runnable() {
      public void run() {       
        // TODO Better error handling
        ServerSocket serverSocket;
        Socket clientSocket;
        try {
          
          // Open socket and listen
          serverSocket = new ServerSocket(1234);
          clientSocket = serverSocket.accept();
          
          // Setup i/o
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(new InputStreamReader(
                                                     clientSocket.getInputStream()));
          String inputLine, outputLine;
          while ((inputLine = in.readLine()) != null) {
            outputLine = inputLine;
            out.println(outputLine);
            Log.i("TCP", "Message Recieved: " + outputLine);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }).start();
  }
  
  private void stopServer() {
    stopSelf();
  }

}
