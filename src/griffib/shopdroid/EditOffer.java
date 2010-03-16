package griffib.shopdroid;

import griffib.shopdroid.lists.OffersList;
import griffib.shopdroid.lists.ProductsList;
import griffib.shopdroid.lists.SDroidList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
//import android.widget.ImageButton;
import android.widget.LinearLayout;

public class EditOffer extends Activity {

  private EditText summary;
  private long productId;
  private long offerId;
  private String[] preds;
  private String[] vals;
  private int numOfAttrs = 0;
  private boolean edit;
  
  /** Called when activity is first created */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_offer);
    
    summary = (EditText) findViewById(R.id.offer_summary);
    
    // Set up buttons
    Button confirmButton = (Button) findViewById(R.id.btn_done);
    Button cancelButton = (Button) findViewById(R.id.btn_discard);
    ImageButton addAttrButton = (ImageButton) findViewById(R.id.add_attr);
    
    // Get product ID
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      edit = extras.getBoolean(SDroidList.EDIT);
      // New offer - just need a product id
      if (!edit) {
        if ((Long) extras.getLong(SDroidDb.KEY_ID) != null)
          productId = extras.getLong(SDroidDb.KEY_ID);
      } 
      // Edit offer - also need the id of the offer!
      else {
        if ((Long) extras.getLong(SDroidDb.KEY_ID) != null)
          offerId = extras.getLong(SDroidDb.KEY_ID);
        if ((Long) extras.getLong(SDroidDb.KEY_PRODUCT_ID) != null)
          productId = extras.getLong(SDroidDb.KEY_PRODUCT_ID);
        if (extras.getStringArray(SDroidDb.KEY_ATTRIBUTES_PREDICATE) != null)
          preds = extras.getStringArray(SDroidDb.KEY_ATTRIBUTES_PREDICATE);
        if (extras.getStringArray(SDroidDb.KEY_ATTRIBUTES_VALUE) != null)
          vals = extras.getStringArray(SDroidDb.KEY_ATTRIBUTES_VALUE);
        if (extras.getString(OffersList.SUMMARY)!=null)
          summary.setText(extras.getString(OffersList.SUMMARY));
        
        for (int i=0;i<preds.length;i++) {
          addAttributeView(numOfAttrs++, preds[i], vals[i]);
        }
      }
      
    } else {
      // Something has gone wrong - there should always be extras!!!
    }
    
    /*
     * Set up button listeners
     */
    

    confirmButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        
        bundle.putString(SDroidDb.KEY_OFFER_SUM,
                         summary.getText().toString());
        
        bundle.putLong(SDroidDb.KEY_ID, productId);
        
        if(edit)
          bundle.putLong(SDroidDb.KEY_OFFER_ID, offerId);
        
        
        // Build arrays of attributes
        String[] tagsAttrName = new String[numOfAttrs];
        String[] tagsAttrVal = new String[numOfAttrs];
        
        // Find attribute names and fill array
        for (int i = 0; i<numOfAttrs; i++) {
          EditText et = (EditText) findViewById(i).findViewById(R.id.attr_name);
          tagsAttrName[i] = et.getText().toString();
        }
        
        // Find attribute values and fill array
        for (int i = 0; i<numOfAttrs; i++) {
          EditText et  =(EditText) findViewById(i).findViewById(R.id.attr_val);
          tagsAttrVal[i] = et.getText().toString();
        }
        
        bundle.putStringArray(SDroidDb.KEY_ATTRIBUTES_PREDICATE, tagsAttrName);
        bundle.putStringArray(SDroidDb.KEY_ATTRIBUTES_VALUE, tagsAttrVal);
        bundle.putInt(ProductsList.KEY_ATTR_NUM, numOfAttrs);
                                     
        Intent i = new Intent();
        i.putExtras(bundle);
        setResult(RESULT_OK, i);
        finish();        
      }
    });
   
   cancelButton.setOnClickListener(new View.OnClickListener() {
    
    @Override
    public void onClick(View v) {
      setResult(RESULT_CANCELED);
      finish();      
    }
   });
   
   addAttrButton.setOnClickListener(new View.OnClickListener() {
    
    @Override
    public void onClick(View v) {
      addAttributeView(numOfAttrs++);
    }
   });     
    
  }
  
  
  private void addAttributeView(int id) {
    LinearLayout attrList = (LinearLayout) findViewById(R.id.attr_list);
    LayoutInflater inflater = getLayoutInflater();
    View row = inflater.inflate(R.layout.attr_row, null);
    LinearLayout extraAttr = (LinearLayout) row.findViewById(R.id.attr_row);
    
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);
    extraAttr.setId(id);
    attrList.addView(extraAttr, id, params);
  }
  
  private void addAttributeView(int id, String pred, String val) {
    
    LinearLayout attrList = (LinearLayout) findViewById(R.id.attr_list);
    LayoutInflater inflater = getLayoutInflater();
    View row = inflater.inflate(R.layout.attr_row, null);
    
    LinearLayout extraAttr = (LinearLayout) row.findViewById(R.id.attr_row);
    EditText predEditText = (EditText) row.findViewById(R.id.attr_name);
    predEditText.setText(pred);
    EditText valEditText = (EditText) row.findViewById(R.id.attr_val);
    valEditText.setText(val);
    
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);
    
    extraAttr.setId(id);
    attrList.addView(extraAttr, id, params);
  }  

}
