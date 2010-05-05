package griffib.shopdroid.comms;

import griffib.shopdroid.SDroidDb;

import java.io.*;
import java.net.*;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SDroidServer extends Service {

  private final static Long time = Long.parseLong("3600000");
  private SDroidClient client;
  
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
    SDroidDb db = new SDroidDb(this);
    startServer(this, db);
    super.onStart(intent, startId);
  }
  
  /**
   * 
   */
  private void startServer(final Context ctx, final SDroidDb db) {
    // Start a new thread - we don't want to hang the UI
    new Thread(new Runnable() {
      public void run() {
        SDroidClient client = new SDroidClient(db, ctx);
        
        while (true) {
          try {
            client.exportOffers();
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          try {
            Thread.sleep(time);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }).start();
  }
  
  private void stopServer() {
    stopSelf();
  }

}
