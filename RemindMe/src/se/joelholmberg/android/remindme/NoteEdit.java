/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.joelholmberg.android.remindme;

import se.joelholmberg.android.remindme.NotesDbAdapter;

import com.example.remindme.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {
	
	private static final int ACTIVITY_ADD_POSITION=0;
	
	private NotesDbAdapter mDbHelper;
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private String mLatitude, mLongitude, mRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.note_edit);
        
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button addPositionButton = (Button) findViewById(R.id.add_position);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ID);
        if (mRowId == null) {
        	
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ID)
                                    : null;
        }
        
        populateFields();
        
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	setResult(RESULT_OK);
                finish();
            }
        });
    
        
        addPositionButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	addPosition();
            }

        });
    }
    
    protected void addPosition() {
    	saveState();
    	Intent i = new Intent(this, PositionService.class);
    	i.putExtra(NotesDbAdapter.KEY_ID, mRowId);
        startActivityForResult(i, ACTIVITY_ADD_POSITION);
	}

	private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        }
    }
    
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ID, mRowId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);

    	switch(requestCode) {
    	case ACTIVITY_ADD_POSITION:
    		//TODO: Inform user that positions were added
    	}
    }
}
