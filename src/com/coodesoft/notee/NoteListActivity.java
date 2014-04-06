/****************************************************************************
**
** Copyright (C) 2012 Coode Software
** Contact: http://www.coodesoft.com/
**
** This file is part of Notee.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
**
****************************************************************************/

package com.coodesoft.notee;

import java.sql.Date;
import java.sql.Time;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NoteListActivity extends Activity {
	DataAdapter m_itemAdapter;
    /** Called when the activity is first created. */
	private String m_strUserId;
	private String m_strUserName;
	private String m_strLoginToken;
	
	private ListView m_listViewNote;
	private Button m_btnRefresh;
	private Button m_btnNew;
	private EditText m_editSearchText;
	private Button m_btnSearch;
	
	private ProgressDialog m_progressDialog = null;
	TaskDelNote m_taskDelNote = null;
	private NoteeNetworkReply m_pNetworkReply = new NoteeNetworkReply();
	private ConnectionDetector m_connectionDetector = null;
	
	private static final int ACTIVITY_EDIT = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        
        Bundle extras = getIntent().getExtras();
        m_strUserId = extras.getString("USER_ID");
        m_strUserName = extras.getString("USER_NAME");
        m_strLoginToken = extras.getString("LOGIN_TOKEN");
        
        m_connectionDetector = new ConnectionDetector(this);
        
        m_listViewNote = (ListView)findViewById(R.id.list_view_notes);
        m_itemAdapter = new DataAdapter( 
				this,
				R.layout.list_item_entry,
				R.layout.list_item_loading,
				m_strUserId,
				m_strUserName,
				m_strLoginToken); 
        m_listViewNote.setAdapter((ListAdapter)m_itemAdapter);
        
        // list view click
        m_listViewNote.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
		    	if(!m_connectionDetector.isConnectingToInternet()) {
		    		Toast.makeText(NoteListActivity.this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
		    		return;
		    	}
		    	
				m_listViewNote.requestFocus();
				NoteItem currentItem = (NoteItem)m_itemAdapter.getItem(position);
				if(currentItem != null)
				{
					Intent intent = new Intent(NoteListActivity.this, NoteEditActivity.class);
					intent.putExtra("USER_ID", m_strUserId);
					intent.putExtra("USER_NAME", m_strUserName);
					intent.putExtra("LOGIN_TOKEN", m_strLoginToken);
					intent.putExtra("NOTE_ID", currentItem.m_nId);
					intent.putExtra("NOTE_LOCAL_ID", currentItem.m_nLocalId);
					startActivityForResult(intent, ACTIVITY_EDIT);
				}
			}
        });
        
      
        registerForContextMenu(m_listViewNote);
        
        // refresh
        m_btnRefresh = (Button)findViewById(R.id.btn_refresh);
        m_btnRefresh.setOnClickListener(new Button.OnClickListener(){   
            public void onClick(View v)  
            {  
		    	if(!m_connectionDetector.isConnectingToInternet()) {
		    		Toast.makeText(NoteListActivity.this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
		    		return;
		    	}
		    	
            	m_itemAdapter.refreshData();
            	m_listViewNote.invalidate();
            	m_listViewNote.requestFocus();
            }  
        }); 
        
        // new
        m_btnNew = (Button) findViewById(R.id.btn_new);
        m_btnNew.setOnClickListener(new Button.OnClickListener(){   
            public void onClick(View v)  
            {
		    	if(!m_connectionDetector.isConnectingToInternet()) {
		    		Toast.makeText(NoteListActivity.this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
		    		return;
		    	}
		    	
				Intent intent = new Intent(NoteListActivity.this, NoteEditActivity.class);
				intent.putExtra("USER_ID", m_strUserId);
				intent.putExtra("USER_NAME", m_strUserName);
				intent.putExtra("LOGIN_TOKEN", m_strLoginToken);
				intent.putExtra("NOTE_ID", -1);
				startActivityForResult(intent, ACTIVITY_EDIT);
            }  
        }); 
        
        // search
        m_editSearchText = (EditText)findViewById(R.id.edit_search_text);
        /*
        m_editSearchText.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					m_btnRefresh.setVisibility(Button.INVISIBLE);
					m_btnNew.setVisibility(Button.INVISIBLE);
				} else {
					m_btnRefresh.setVisibility(Button.VISIBLE);
					m_btnNew.setVisibility(Button.VISIBLE);
				}				
			}
        });
        */
        
        m_btnSearch = (Button)findViewById(R.id.btn_search);
        m_btnSearch.setOnClickListener(new Button.OnClickListener(){   
            public void onClick(View v)  
            {
		    	if(!m_connectionDetector.isConnectingToInternet()) {
		    		Toast.makeText(NoteListActivity.this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
		    		return;
		    	}
		    	
                String strSearch = m_editSearchText.getText().toString().trim();
            	m_itemAdapter.searchText(strSearch);
            	m_listViewNote.requestFocus();
            }  
        }); 
        
        m_listViewNote.requestFocus();
    }
    
    @Override
    public void onStop() {
    	if(m_progressDialog != null)
    		m_progressDialog.dismiss();
    	super.onStop();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        /*
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
        return;
        }

        Something something = (Subway) getListAdapter().getItem(info.position);
        menu.setHeaderTitle(something.getName());
        menu.setHeaderIcon(something.getIcon());
        menu.add(0, CONTEXT_MENU_SHARE, 0, "Do something!");
        */
        
        if (v.getId() == R.id.list_view_notes) {
            /*
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(Countries[info.position]);
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
              menu.add(Menu.NONE, i, i, menuItems[i]);
            }
            */
        	
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit, menu);
        }
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	// context menu
    	if(null != info) {
        	if(!m_connectionDetector.isConnectingToInternet()) {
        		Toast.makeText(this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
        		return true;
        	}
        	
        	NoteItem noteItem = (NoteItem)m_itemAdapter.getItem((int)info.id);
    		switch(item.getItemId()) {
    		case R.id.item_edit:
				Intent intent = new Intent(NoteListActivity.this, NoteEditActivity.class);
				intent.putExtra("USER_ID", m_strUserId);
				intent.putExtra("USER_NAME", m_strUserName);
				intent.putExtra("LOGIN_TOKEN", m_strLoginToken);
				intent.putExtra("START_EDITING", true);
				intent.putExtra("NOTE_ID", noteItem.m_nId);
				intent.putExtra("NOTE_LOCAL_ID", noteItem.m_nLocalId);
				startActivityForResult(intent, ACTIVITY_EDIT);
    			break;
    		case R.id.item_del:
    		    if(m_progressDialog != null)
    		    	m_progressDialog.dismiss();
    	        m_progressDialog = new ProgressDialog(this) {
    	        	@Override
    	        	public void onStop() {
    	        		if(m_taskDelNote != null)
    	        			m_taskDelNote.cancel(true);
    	        		m_taskDelNote = null;
    	        	}
    	        };
    			m_progressDialog.setTitle("Delete note");
    			m_progressDialog.setMessage("Please waiting...");
    			m_progressDialog.show();
    			m_taskDelNote = new TaskDelNote();
    			m_taskDelNote.m_currentNoteItem = noteItem;
    			m_taskDelNote.execute((Void)null);
    			break;
			default:
				break;
    		}
        	
        	return true;
    	}
    	
    	// options menu
        switch(item.getItemId())
        {
        case R.id.item_options:
        {
			Intent intent = new Intent(NoteListActivity.this, OptionActivity.class);
			startActivity(intent);
            break;
        }
        case R.id.item_logout:
        {
			Intent intent = new Intent(NoteListActivity.this, LoginActivity.class);
			intent.putExtra("DONT_AUTO_LOGIN", true);
			startActivity(intent);
			finish();
            break;
        }
        case R.id.item_exit:
        	finish();
            break;
        default:
        	break;
        }
    	/*
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case DO_SOMETHING:
                /* Do sothing with the id */
       //         Something something = getListAdapter().getItem(info.position);
      //          return true;
     //   }
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(ACTIVITY_EDIT == requestCode)
    	{
    		if(RESULT_OK == resultCode)
    		{
  //  			Toast.makeText(getApplicationContext(), "result", Toast.LENGTH_LONG).show();
    			
    			NoteItem noteItem = new NoteItem();
    			String strAction = data.getStringExtra("ACTION");
    			noteItem.m_nLocalId = data.getIntExtra("NOTE_LOCAL_ID", -1);
    			
    			// delete
    			if(strAction.equalsIgnoreCase("DELETE")) {
    				m_itemAdapter.deleteNoteItem(noteItem.m_nLocalId);
    				return;
    			}    			
    			
    			noteItem.m_nId = data.getIntExtra("NOTE_ID", -1);
    			noteItem.m_nType = data.getIntExtra("NOTE_TYPE", 1);
    			noteItem.m_strTitle = data.getStringExtra("NOTE_TITLE");
    			noteItem.setSetAt(data.getStringExtra("NOTE_SET_AT"));
    			noteItem.setUpdatedAt(data.getStringExtra("NOTE_UPDATED_AT"));
    			
    			// new
    			if(strAction.equalsIgnoreCase("NEW")) {
    				// created at
        			noteItem.setCreatedAt(data.getStringExtra("NOTE_CREATED_AT"));
    				m_itemAdapter.addNoteItem(noteItem);
    			// update
    			} else if(strAction.equalsIgnoreCase("UPDATE")) {
    				m_itemAdapter.updateNoteItem(noteItem);
    			}
    		}
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu_activity_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
        case R.id.item_options:
        {
			Intent intent = new Intent(NoteListActivity.this, OptionActivity.class);
			startActivity(intent);
            break;
        }
        case R.id.item_logout:
        {
			Intent intent = new Intent(NoteListActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
            break;
        }
        case R.id.item_exit:
        	finish();
            break;
        default:
        	break;
        }
        return true;
    }
    
    // task
    private class TaskDelNote extends AsyncTask<Void, Void, Boolean> {
    	NoteItem m_currentNoteItem;

		@Override
		protected Boolean doInBackground(Void... arg0) {
			if(m_pNetworkReply.deleteNoteItem(m_strUserId, m_strLoginToken, m_currentNoteItem)) {
				if(m_pNetworkReply.getResultString().equalsIgnoreCase("0")) {
					return true;
				} else {
					Toast.makeText(getApplicationContext(), m_pNetworkReply.getResultString(), Toast.LENGTH_LONG).show();
				}
			}
			
			return false;
		}
		
		@Override
        protected void onPostExecute(final Boolean success) {
			if(m_progressDialog != null)
				m_progressDialog.hide();
			m_itemAdapter.deleteNoteItem(m_currentNoteItem.m_nLocalId);
		}
		
        @Override
        protected void onCancelled() {
        	if(m_progressDialog != null)
        		m_progressDialog.hide();
        }    	
    }
}
