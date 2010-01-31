package griffib.shopdroid;

import java.io.*;
import java.net.*;

import android.util.Log;

public class SDroidClient {

  private final String msg;
  private final String server;
  
  Socket SDClient = null;
  PrintWriter out = null;
  BufferedReader in = null;
  
  /**
   * Takes message to send and a server to send it to.
   * @param msg, server
   */
  public SDroidClient(String msg, String server) {
    this.msg = msg;
    this.server = server;
    try {
      init();
    } catch (IOException e) {
      Log.e("Server", e.toString());
    }
  }
  
  /**
   * Initialise the socket and i/o, called by constructor
   * @throws IOException
   */
  private void init() throws IOException {
    SDClient = new Socket(server, 1234);
    out = new PrintWriter(SDClient.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(SDClient.getInputStream()));
  }
  
  /**
   * Sends the message then closes the connection.
   */
  public void send() {
    //while (msg != null) {
      out.println(msg);
      System.out.println("Client TCP: Recieved: " + msg);
    //}
    
    //Message sent, close connection
    try {
      out.close();
      in.close();
      SDClient.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
