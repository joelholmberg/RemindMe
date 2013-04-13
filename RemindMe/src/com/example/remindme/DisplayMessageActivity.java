package com.example.remindme;

import java.io.DataInputStream;
import java.io.FileInputStream;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        
        
        //get message from file
        FileInputStream fis;
        final StringBuffer storedString = new StringBuffer();

        try {
            fis = openFileInput("savefile.txt");
            DataInputStream dataIO = new DataInputStream(fis);
            String strLine = null;

            while ((strLine = dataIO.readLine()) != null) {
                storedString.append(strLine);
                System.out.println("Read string: " + strLine);
            }
            System.out.println("No more strings");
            dataIO.close();
            fis.close();
        }
        catch  (Exception e) {  
        }
        //Create a text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(storedString);
        
        //Set the text view as the activity layout
        setContentView(textView);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display_message, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
