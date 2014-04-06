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

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

public class NoteEditActivity extends Activity {
	
	private Calendar m_calendar = Calendar.getInstance();
	private ProgressDialog m_progressDialog = null;
	private String m_strUserId;
	private String m_strLoginToken;
	private NoteItem m_noteItem = new NoteItem();
	private boolean m_bStartEditing = false;
	private boolean m_bNew = true;
	private int m_nNoteLocalId = -1;
	
	private Button m_btnCancel;
	private Button m_btnFinish;
	private Button m_btnPic;
	private Button m_btnCamera;
	private Button m_btnEdit;
	private Button m_btnDel;
	private RadioButton m_btnRadioNote;
	private RadioButton m_btnRadioSchedule;
	private EditText m_editTextSetAtDate;
	private EditText m_editTextSetAtTime;
	private EditText m_editTitle;
	private EditText m_editContent;
	
	private NoteeNetworkReply m_pNetworkReply = new NoteeNetworkReply();
	GetNoteTask m_getNoteTask = null;
	TaskDelNote m_taskDelNote = null;
	UpdateNoteItemTask m_updateNoteItemTask = null;
	private ConnectionDetector m_connectionDetector = null;
	
	private static final int DIALOG_DATE = 1;
	private static final int DIALOG_TIME = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        
        Bundle extras = getIntent().getExtras();
        m_strUserId = extras.getString("USER_ID");
        m_strLoginToken = extras.getString("LOGIN_TOKEN");
        m_noteItem.m_nId = extras.getInt("NOTE_ID");
        m_nNoteLocalId = extras.getInt("NOTE_LOCAL_ID", -1);
        m_bStartEditing = extras.getBoolean("START_EDITING");
        
        m_connectionDetector = new ConnectionDetector(this);
         
        // start loading note item
        if(m_noteItem.m_nId >= 0)
        {  
        	m_bNew = false;
        	
	    	if(m_connectionDetector.isConnectingToInternet()) {
	        	m_progressDialog = new ProgressDialog(this) {
	        		@Override
	        		public void onStop() {
	        			if(m_getNoteTask != null)
	        				m_getNoteTask.cancel(true);
	        		}
	        	};
	            m_progressDialog.setTitle("Get note");
	            m_progressDialog.setMessage("Please waiting...");
	            m_progressDialog.show();
	            
	            m_getNoteTask = new GetNoteTask();
	            m_getNoteTask.execute((Void)null);
	    	} else {
	    		Toast.makeText(NoteEditActivity.this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
	    	}

        } else {
        	// new note
        	m_noteItem.m_nId = -1;
        	m_bStartEditing = true;
        	m_bNew = true;
        }
        
        // cancel
        m_btnCancel = (Button)findViewById(R.id.btn_cancel);
        m_btnCancel.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();				
			}
        	
        });
        
        // finish
        m_btnFinish = (Button)findViewById(R.id.btn_finish);
        m_btnFinish.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
		    	if(!m_connectionDetector.isConnectingToInternet()) {
		    		Toast.makeText(NoteEditActivity.this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
		    		return;
		    	}
				
				String strTitle = m_editTitle.getText().toString().trim();
				
	            if (TextUtils.isEmpty(strTitle)) {
	            	m_editTitle.setError(getString(R.string.str_error_empty_title));
	                m_editTitle.requestFocus();
	                return;
	            }
	            
	            m_progressDialog = new ProgressDialog(NoteEditActivity.this) {
	            	@Override
	            	public void onStop() {
	            		if(m_updateNoteItemTask != null)
	            			m_updateNoteItemTask.cancel(true);
	            		m_updateNoteItemTask = null;
	            	}
	            };       
				
				// update
		        if(m_noteItem.m_nId >= 0)
		        {
		            m_progressDialog.setTitle("Update note");
		        } else {
		        // new
		            m_progressDialog.setTitle("Create note");
		        }
		        
	            m_progressDialog.setMessage("Please waiting...");
	            m_progressDialog.show();
		        
		        updateNoteData();
		        m_updateNoteItemTask = new UpdateNoteItemTask();
		        m_updateNoteItemTask.execute((Void)null);
			}
        });
        
        // picture
        m_btnPic = (Button)findViewById(R.id.btn_pic);
        m_btnPic.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        // camera
        m_btnCamera = (Button)findViewById(R.id.btn_camera);
        m_btnCamera.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        // edit
        m_btnEdit = (Button)findViewById(R.id.btn_edit);
        m_btnEdit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				setStartEditing(true);
			}
        	
        });
        
        // delete
        m_btnDel = (Button)findViewById(R.id.btn_del);
        m_btnDel.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
		    	if(!m_connectionDetector.isConnectingToInternet()) {
		    		Toast.makeText(NoteEditActivity.this, getString(R.string.str_error_no_network), Toast.LENGTH_LONG).show();
		    		return;
		    	}
		    	
	        	m_progressDialog = new ProgressDialog(NoteEditActivity.this) {
	        		@Override
	        		public void onStop() {
	        			if(m_taskDelNote != null)
	        				m_taskDelNote.cancel(true);
	        		}
	        	};
    			m_progressDialog.setTitle("Delete note");
    			m_progressDialog.setMessage("Please waiting...");
    			m_progressDialog.show();
    			m_taskDelNote = new TaskDelNote();
    			m_taskDelNote.execute((Void)null);
			}
        	
        });
        
        if(m_noteItem.m_nId < 0)
        	m_btnDel.setVisibility(Button.INVISIBLE);
        
        // radio button note
        m_btnRadioNote = (RadioButton)findViewById(R.id.radio_btn_note);
        m_btnRadioNote.setOnClickListener(new RadioButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				m_btnRadioSchedule.setChecked(false);
				m_editTextSetAtTime.setVisibility(EditText.INVISIBLE);
			}
        	
        });
        
        // radio button schedule
    	m_btnRadioSchedule = (RadioButton)findViewById(R.id.radio_btn_schedule);
    	m_btnRadioSchedule.setOnClickListener(new RadioButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				m_btnRadioNote.setChecked(false);
				m_editTextSetAtTime.setVisibility(EditText.VISIBLE);
			}
        	
        });
    	
    	m_editTitle = (EditText)findViewById(R.id.edit_title);
    	m_editContent = (EditText)findViewById(R.id.edit_content);
    	
    	m_editTextSetAtDate = (EditText)findViewById(R.id.edit_date);
    	m_editTextSetAtDate.setOnTouchListener(new EditText.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(m_bStartEditing)
					showDialog(DIALOG_DATE);
				return true;
			}
    		
    	});
    	
    	m_editTextSetAtTime = (EditText)findViewById(R.id.edit_time);
    	m_editTextSetAtTime.setOnTouchListener(new EditText.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(m_bStartEditing)
					showDialog(DIALOG_TIME);
				return true;
			}
    		
    	});
    	
    	setStartEditing(m_bStartEditing);
        initControls();
        m_btnEdit.setVisibility(Button.INVISIBLE);
        m_btnDel.setVisibility(Button.INVISIBLE);
    }
    
    @Override
    public void onStop() {
    	if(m_progressDialog != null)
    		m_progressDialog.dismiss();
    	super.onStop();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case DIALOG_DATE:
    	{
    		Date dt = Utils.parseDate2Date(m_editTextSetAtDate.getText().toString());
    		return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {

					Date dt = new Date(0);
					dt.setYear(year - 1900);
					dt.setMonth(monthOfYear);
					dt.setDate(dayOfMonth);
					m_editTextSetAtDate.setText(Utils.toDate(dt));					
				}
    			
    		}, 
    		dt.getYear() + 1900, dt.getMonth(), dt.getDate());
    	}    		
    	case DIALOG_TIME:
    	{
    		Date tm = new Date(0);
    		tm.setHours(m_calendar.get(Calendar.HOUR_OF_DAY));
    		tm.setMinutes(m_calendar.get(Calendar.MINUTE));
    		String str = m_editTextSetAtTime.getText().toString();
    		if(str.length() > 0) {
    			tm = Utils.parseTime2Time(str);
    		}    		
    		return new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					Date tm = new Date(0);
					tm.setHours(hourOfDay);
					tm.setMinutes(minute);
					m_editTextSetAtTime.setText(Utils.toTime(tm));				
				}
			}, tm.getHours(), tm.getMinutes(), DateFormat.is24HourFormat(this));
    	}    		
    	default:
    			break;
    	}
		return null;
    }

    private void initControls() {
    	m_btnRadioNote.setChecked(true);
    	m_btnRadioSchedule.setChecked(false);
    	
    	// initial set at
		Date dt = new Date(0);
		dt.setYear(m_calendar.get(Calendar.YEAR) - 1900);
		dt.setMonth(m_calendar.get(Calendar.MONTH));
		dt.setDate(m_calendar.get(Calendar.DATE));
		m_editTextSetAtDate.setText(Utils.toDate(dt));
		Date tm = new Date(0);
		tm.setHours(m_calendar.get(Calendar.HOUR_OF_DAY));
		tm.setMinutes(m_calendar.get(Calendar.MINUTE));
		m_editTextSetAtTime.setText(Utils.toTime(tm));
    	
		m_editTextSetAtTime.setVisibility(EditText.INVISIBLE);    	
    }
    
    private void initControlsWithNoteItem() {
    	m_editTitle.setText(m_noteItem.m_strTitle);
    	
    	m_btnRadioNote.setChecked(m_noteItem.isNote());
    	m_btnRadioSchedule.setChecked(!m_noteItem.isNote());
    	
    	m_editTextSetAtDate.setText(m_noteItem.getSetAtDate());

    	m_editContent.setText(Html.fromHtml(m_noteItem.m_strContent, new MyImageGetter(), new MyHtmlTagHandler()));
    	//editContent.setText(Html.fromHtml(m_noteItem.m_strContent, null, new MyHtmlTagHandler()));
    	
    	if(m_noteItem.isNote()) {
    		m_editTextSetAtTime.setVisibility(EditText.INVISIBLE);
    	} else {
    		m_editTextSetAtTime.setVisibility(EditText.VISIBLE);
    		m_editTextSetAtTime.setText(m_noteItem.getSetAtTime());
    		m_btnRadioNote.setVisibility(RadioButton.INVISIBLE);
    		m_btnRadioSchedule.setVisibility(RadioButton.VISIBLE);
    	}
    }
    
    private void updateNoteData() {
    	m_noteItem.m_strTitle = m_editTitle.getText().toString();
    	
    	if(m_btnRadioNote.isChecked())
    		m_noteItem.m_nType = NoteItem.TYPE_NOTE;
    	else
    		m_noteItem.m_nType = NoteItem.TYPE_SCHEDULE;
    	
    	m_noteItem.m_strContent = Html.toHtml(m_editContent.getText());
    	
    	// set at
    	String strDateTime;
    	if(m_noteItem.isNote()) {
    		strDateTime = m_editTextSetAtDate.getText().toString();
    		
    	} else {
    		strDateTime = m_editTextSetAtDate.getText().toString().trim() + " " + m_editTextSetAtTime.getText().toString().trim();
    	}
    	m_noteItem.setSetAt(strDateTime);
    	// updated at
    	m_noteItem.m_dtUpdatedAt.setYear(m_calendar.get(Calendar.YEAR) - 1900);
    	m_noteItem.m_dtUpdatedAt.setMonth(m_calendar.get(Calendar.MONTH));
    	m_noteItem.m_dtUpdatedAt.setDate(m_calendar.get(Calendar.DAY_OF_MONTH));
    	m_noteItem.m_dtUpdatedAt.setHours(m_calendar.get(Calendar.HOUR_OF_DAY));
    	m_noteItem.m_dtUpdatedAt.setMinutes(m_calendar.get(Calendar.MINUTE));
    	// created at
    	if(m_bNew) {
        	m_noteItem.m_dtCreatedAt = m_noteItem.m_dtUpdatedAt;
    	}
    	
    	m_noteItem.m_strContent = Html.toHtml(m_editContent.getText());
    }
    
    private void setStartEditing(boolean bVal) {
    	m_bStartEditing = bVal;
    	// editing
    	if(m_bStartEditing)
    	{
    		m_btnCancel.setText(getString(R.string.str_cancel));
    		
	        m_btnPic.setVisibility(Button.INVISIBLE);
	        m_btnCamera.setVisibility(Button.INVISIBLE);
	        
	        m_btnRadioNote.setVisibility(EditText.VISIBLE);
	        m_btnRadioSchedule.setVisibility(EditText.VISIBLE);
	        
	        m_btnEdit.setVisibility(Button.INVISIBLE);
	        m_btnDel.setVisibility(Button.INVISIBLE);
	        m_btnFinish.setVisibility(Button.VISIBLE);
	        
	    	m_editTitle.setEnabled(true);
	    	m_btnRadioNote.setEnabled(true);
	    	m_btnRadioSchedule.setEnabled(true);
	    	m_editTextSetAtDate.setEnabled(true);
	    	m_editTextSetAtTime.setEnabled(true);
	    	m_editContent.setEnabled(true);
	    // browse
    	} else {
    		m_btnCancel.setText(getString(R.string.str_return));
    		
            m_btnPic.setVisibility(Button.INVISIBLE);
            m_btnCamera.setVisibility(Button.INVISIBLE);
            
	        m_btnEdit.setVisibility(Button.VISIBLE);
	        m_btnDel.setVisibility(Button.VISIBLE);
	        m_btnFinish.setVisibility(Button.INVISIBLE);
	        
	    	m_editTitle.setEnabled(false);
	    	m_btnRadioNote.setEnabled(false);
	    	m_btnRadioSchedule.setEnabled(false);
	    	m_editTextSetAtDate.setEnabled(false);
	    	m_editTextSetAtTime.setEnabled(false);
	    	m_editContent.setEnabled(false);
	    	
	    	if(m_noteItem.isNote()) {
	    		m_btnRadioNote.setVisibility(EditText.VISIBLE);
	    		m_btnRadioSchedule.setVisibility(EditText.INVISIBLE);
	    	} else {
	    		m_btnRadioNote.setVisibility(EditText.INVISIBLE);
	    		m_btnRadioSchedule.setVisibility(EditText.VISIBLE);
	    	}
    	}     	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu_activity_edit, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
        case R.id.item_cancel:
        	finish();
            break;
        default:
        	break;
        }
        return true;
    }
    
    // get note task
    public class GetNoteTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			if(m_pNetworkReply.getNoteItem(m_strUserId, m_strLoginToken, m_noteItem.m_nId))
			{
				return true;
			}
			return false;
		}
    	
		@Override
        protected void onPostExecute(final Boolean success) {
			if(success)
			{
				String strResult = m_pNetworkReply.getResultString();
				if(strResult.compareTo("0") == 0)
				{
					m_noteItem.m_nId = Integer.valueOf(m_pNetworkReply.getValue("note_id"));
					m_noteItem.m_strTitle = m_pNetworkReply.getValue("title");
					m_noteItem.m_nType = Integer.valueOf(m_pNetworkReply.getValue("type"));
					m_noteItem.m_nContentSize = Integer.valueOf(m_pNetworkReply.getValue("content_size"));
					m_noteItem.m_strContent = m_pNetworkReply.getValue("content");
					m_noteItem.m_nLocalId = m_nNoteLocalId;
					
					m_noteItem.m_strMD5 = m_pNetworkReply.getValue("md5");
					// set at
					String strDateTime = m_pNetworkReply.getValue("set_at");
					m_noteItem.setSetAt(strDateTime);
					// created at
					strDateTime = m_pNetworkReply.getValue("created_at");
					m_noteItem.setCreatedAt(strDateTime);;
					// updated at
					strDateTime = m_pNetworkReply.getValue("updated_at");
					m_noteItem.setUpdatedAt(strDateTime);
					m_noteItem.m_nUserId = Integer.valueOf(m_pNetworkReply.getValue("user_id"));
					
					initControlsWithNoteItem();
		            m_btnEdit.setVisibility(Button.VISIBLE);
		            m_btnDel.setVisibility(Button.VISIBLE);
				}
				else if(strResult.compareTo("1") == 0)
				{
					// error login token
					Toast.makeText(getApplicationContext(), getString(R.string.str_error_login_token), Toast.LENGTH_LONG).show();
				}
				else if(strResult.compareTo("2") == 0)
				{
					// cannot find note id
					Toast.makeText(getApplicationContext(), getString(R.string.str_error_note_id), Toast.LENGTH_LONG).show();
				}
				else if(strResult.compareTo("3") == 0)
				{
					// no permission
					Toast.makeText(getApplicationContext(), getString(R.string.str_error_no_permission), Toast.LENGTH_LONG).show();
				}
			}
			
			m_progressDialog.hide();
		}
		
		protected void onCancelled() {
			m_progressDialog.hide();
		}
    }
    
    // update note item task
    public class UpdateNoteItemTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			if(m_pNetworkReply.updateNoteItem(m_strUserId, m_strLoginToken, m_noteItem))
			{
				return true;
			}
			return false;
		}
    	
		@Override
        protected void onPostExecute(final Boolean success) {
			if(success)
			{
				String strResult = m_pNetworkReply.getResultString();
				if(strResult.compareTo("0") == 0)
				{
					m_progressDialog.hide();
					
					Intent intent = new Intent();
					if(m_bNew) {
						intent.putExtra("ACTION", "NEW");
						String strNoteId = m_pNetworkReply.getValue("note_id");
						m_noteItem.m_nId = Integer.valueOf(strNoteId);
					}
					else
						intent.putExtra("ACTION", "UPDATE");
					
					intent.putExtra("NOTE_ID", m_noteItem.m_nId);
					intent.putExtra("NOTE_LOCAL_ID", m_noteItem.m_nLocalId);
					intent.putExtra("NOTE_TITLE", m_noteItem.m_strTitle);
					intent.putExtra("NOTE_TYPE", m_noteItem.m_nType);
					intent.putExtra("NOTE_SET_AT", m_noteItem.getSetAt());
					intent.putExtra("NOTE_CREATED_AT", m_noteItem.getCreatedAt());
					intent.putExtra("NOTE_UPDATED_AT", m_noteItem.getCreatedAt());
					
					setResult(RESULT_OK, intent);
					
					finish();
				}
				else if(strResult.compareTo("1") == 0)
				{
					// error login token
					Toast.makeText(getApplicationContext(), getString(R.string.str_error_login_token), Toast.LENGTH_LONG).show();
				}
				else if(strResult.compareTo("2") == 0)
				{
					// cannot find note id
					Toast.makeText(getApplicationContext(), getString(R.string.str_error_note_id), Toast.LENGTH_LONG).show();
				}
				else if(strResult.compareTo("3") == 0)
				{
					// no permission
					Toast.makeText(getApplicationContext(), getString(R.string.str_error_no_permission), Toast.LENGTH_LONG).show();
				}
			}
			
			m_progressDialog.hide();
		}
		
		protected void onCancelled() {
			m_progressDialog.hide();
		}
    }
    
    
    // delete note
    private class TaskDelNote extends AsyncTask<Void, Void, Boolean> {

    	@Override
		protected Boolean doInBackground(Void... arg0) {
			if(m_pNetworkReply.deleteNoteItem(m_strUserId, m_strLoginToken, m_noteItem)) {
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
			if(success)
			{
				String strResult = m_pNetworkReply.getResultString();
				if(strResult.compareTo("0") == 0)
				{
					updateNoteData();

					Intent intent = new Intent();
					intent.putExtra("ACTION", "DELETE");
					intent.putExtra("NOTE_LOCAL_ID", m_noteItem.m_nLocalId);
					setResult(RESULT_OK, intent);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), strResult, Toast.LENGTH_LONG).show();
				}
			}
			
			m_progressDialog.hide();
		}
		
        @Override
        protected void onCancelled() {
        	m_progressDialog.hide();
        }    	
    }
}
