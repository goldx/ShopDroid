package griffib.shopdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.TextView;

public class ShopDroid extends Activity {
  
  private static final int DIALOG_MSG_SENT = 0;
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    // Find button
    Button sendMsg = (Button)findViewById(R.id.message_button);
    sendMsg.setOnClickListener(msgButtonListener);
    
    // Start some services
    Intent startServer = new Intent(this, SDroidServer.class);
    this.startService(startServer);
  }
  
  /**
   * Create dialogs
   */
  protected Dialog onCreateDialog(int id) {
    AlertDialog msgSentDialog;
    switch(id) {
      case DIALOG_MSG_SENT:
         AlertDialog.Builder buildMsgSent = new AlertDialog.Builder(this);
         buildMsgSent.setMessage(R.string.msg_sent)
                     .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                      
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();                        
                      }
                    });
         msgSentDialog = buildMsgSent.create();
         
        break;
      default:
        msgSentDialog = null;
    }
    return msgSentDialog;
  }
  
  /**
   * Listener for sendMsg button
   */
  private OnClickListener msgButtonListener = new OnClickListener() {
  
    // On a click send a message and tell the user
    public void onClick(View v) {
      SDroidClient msg = new SDroidClient("Test Message", "localhost");
      msg.send();
      // Message dialog
      showDialog(DIALOG_MSG_SENT);
    }
  
  };
}