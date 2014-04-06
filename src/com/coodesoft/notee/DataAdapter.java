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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DataAdapter extends BaseAdapter {
	
    private NoteeNetworkReply m_pNetworkReply = new NoteeNetworkReply();
	
    private Context context;
	private int itemRowResID;
    private int loadingRowResID;
    private int base;
    private int pageLen;
    private int m_nNoteAmount;
    int m_nItemsLoaded;
    int m_nItemsToLoad;
    boolean m_bAllItemsLoaded;
    Boolean loading;
    UIUpdateTask updateTask;
    static final int PRELOAD_ITEMS = 30;
    static final String LOG_TAG = "PAGEADAPTER";
    private String m_strUserId = "";
    private String m_strUserName = "";
    private String m_strLoginToken = "";
    private String m_strLimit = "";
    private String m_strSearchText = "";

    ArrayList<NoteItem> m_lstNoteItems = new ArrayList<NoteItem>();
    Handler uiHandler = new Handler();
    LoadingThread m_threadLoading = null;

    public DataAdapter( Context context, 
                        int itemRowResID,
                        int loadingRowResID,
                        String strUserId,
                        String strUserName,
                        String strLoginToken) { 
        this.context = context;
		this.itemRowResID = itemRowResID;
        this.loadingRowResID = loadingRowResID;
        m_nNoteAmount = -1;
        m_nItemsLoaded = 0;
        m_nItemsToLoad = 0;
        m_bAllItemsLoaded = false;
        loading = Boolean.FALSE;
        updateTask = new UIUpdateTask();
        m_strUserId = strUserId;
        m_strUserName = strUserName;
        m_strLoginToken = strLoginToken;
        m_strLimit = Integer.toString(PRELOAD_ITEMS);
    }

    public int getCount() {
    	if(m_nNoteAmount < 0)
    	{
        	if(m_pNetworkReply.getNoteAmount(m_strUserId, m_strLoginToken, m_strSearchText))
        	{
        		if(m_pNetworkReply.getResultString().compareTo("0") == 0)
        		{
        			m_nNoteAmount = Integer.valueOf(m_pNetworkReply.getValue("amount"));
        			m_nItemsLoaded = 0;
        			if(m_nNoteAmount == 0)
        				m_bAllItemsLoaded = true;
        		}
        		else
        			m_nNoteAmount = 0;
        	}
    	}

    	if(m_bAllItemsLoaded)
    		return m_nItemsLoaded;
    	
    	return m_nItemsLoaded + 1;
    }

    public Object getItem(int position) {
        NoteItem result;
        synchronized( m_lstNoteItems ) {
            result = m_lstNoteItems.get( position );
        }
        return result;
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
        boolean isLastRow = position >= m_nItemsLoaded;
        int rowResID = isLastRow ?
                    loadingRowResID :
                    itemRowResID;
        LayoutInflater inflater = LayoutInflater.from( context );
		View v = inflater.inflate(  rowResID, parent, false );
        if( isLastRow ) {
            if ( position < m_nNoteAmount ) {
// Should there be more items loaded?
                int nextItemToLoad = position + PRELOAD_ITEMS;
                if( nextItemToLoad > m_nNoteAmount )
                    nextItemToLoad = m_nNoteAmount;
                Log.d( LOG_TAG, "nextItemToLoad: "+nextItemToLoad );
                if( nextItemToLoad > m_nItemsLoaded ) {
                    Log.d( LOG_TAG, "itemsToLoad: "+ nextItemToLoad );
// Launch the loading thread if it is not currently running
                    synchronized( loading ) {
                        if( !loading.booleanValue() ) {
                            Log.d( LOG_TAG, "Staring loading task" );
                            loading = Boolean.TRUE;
                            m_threadLoading = new LoadingThread();
                            m_threadLoading.start();
                            Log.d( LOG_TAG, "Loading task started" );
                        }
                    }
                }
            } else
                uiHandler.post( updateTask );
        } else {
        	synchronized( m_lstNoteItems ) {
	        	NoteItem item = m_lstNoteItems.get( position );
	        	// title
			    TextView itemControl = (TextView)v.findViewById( R.id.note_item_title );
			    itemControl.setText( item.m_strTitle );
				    
			    // set at
			    TextView itemDate = (TextView)v.findViewById(R.id.note_item_subtitle);
			    itemDate.setText(item.getSetAt());
			    
			    // icon
			    ImageView imageView = (ImageView)v.findViewById(R.id.image_view_icon); 
			    if(item.isNote())
			    	imageView.setImageResource(R.drawable.ic_note);
			    else
			    	imageView.setImageResource(R.drawable.ic_schedule);
        	}
        }
        return v;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean  isEnabled(int position) {
        return true;
    }
    
    public void refreshData() {
    	synchronized(m_lstNoteItems)
    	{
        	m_nNoteAmount = -1;
            m_nItemsLoaded = 0;
            m_nItemsToLoad = 0;
            m_bAllItemsLoaded = false;
            loading = Boolean.FALSE;
            m_lstNoteItems.clear();
            if(m_threadLoading != null) {
                m_threadLoading.stop();
                m_threadLoading = null;
            }
            notifyDataSetChanged();
    	}
    }
    
    public void searchText(String strSearch) {
    	m_strSearchText = strSearch;
    	refreshData();
    }

    public void addNoteItem(NoteItem noteItem) {
    	synchronized( m_lstNoteItems ) {
	    	NoteItem noteItemTmp = null;
	    	boolean bAdded = false;
	    	for(int i = 0; i < m_lstNoteItems.size(); ++i) {
	    		noteItemTmp = (NoteItem)m_lstNoteItems.get(i);
	    		if(!noteItemTmp.m_dtSetAt.after(noteItem.m_dtSetAt)) {
	    			m_lstNoteItems.add(i, noteItem);
	    			noteItem.m_nLocalId = i;
	    			
	    			for(int j = i + 1; j < m_lstNoteItems.size(); ++j)
	    			{
	    				noteItemTmp = (NoteItem)m_lstNoteItems.get(j);
	    				noteItemTmp.m_nLocalId = j;
	    			}
	    			bAdded = true;
	    			break;
	    		}
	    	}
	    	
	    	if(!bAdded) {
	    		noteItem.m_nLocalId = m_lstNoteItems.size();
	    		m_lstNoteItems.add(m_lstNoteItems.size(), noteItem);
	    	}
	    	
	    	m_nNoteAmount++;
	    	m_nItemsLoaded++;
	    	notifyDataSetChanged();
    	}
    }
    
    public void updateNoteItem(NoteItem noteItem) {
    	synchronized( m_lstNoteItems ) {
	    	if(noteItem.m_nLocalId >= 0 && noteItem.m_nLocalId < m_lstNoteItems.size()) {
	    		m_lstNoteItems.set(noteItem.m_nLocalId, noteItem);
	    		notifyDataSetChanged();
	    	}
    	}
    }
    
    public void deleteNoteItem(int nLocalId) {
    	synchronized( m_lstNoteItems ) {
	    	if(nLocalId >= 0 && 
	    			nLocalId < m_lstNoteItems.size()) {
	    		NoteItem noteItemTmp = null;
	    		m_lstNoteItems.remove(nLocalId);
	    		for(int i = nLocalId; i < m_lstNoteItems.size(); ++i)
	    		{
	    			noteItemTmp = (NoteItem)m_lstNoteItems.get(i);
	    			noteItemTmp.m_nLocalId = i;
	    		}
	    		
		    	m_nNoteAmount--;
		    	m_nItemsLoaded--;
		    	notifyDataSetChanged();
	    	}
    	}
    }
    
    // loading thread
    class LoadingThread extends Thread {
        public void run() {
           
            if(!m_pNetworkReply.getNoteListByOffset(m_strUserId, m_strLoginToken, m_strSearchText, Integer.toString(m_nItemsLoaded), m_strLimit))
            {
            	return;
            }
            
            if(m_pNetworkReply.getResultString().compareTo("0") != 0)
            {
                synchronized( loading ) {
                    loading = Boolean.FALSE;
                    m_bAllItemsLoaded = true;
                    uiHandler.post( updateTask );
                }
                return;
            }
            
            JSONArray arrNotes = m_pNetworkReply.getJSONArray("notes");
            JSONObject jsObj = null;
            
            if(arrNotes == null)
            {
            	jsObj = m_pNetworkReply.getJSONObject("notes");
            	
            	if(jsObj == null)
            	{
                    synchronized( loading ) {
                        loading = Boolean.FALSE;
                        m_bAllItemsLoaded = true;
                        uiHandler.post( updateTask );
                    }
                    return;
            	}
            }

            JSONObject jsObject;
            NoteItem noteItem;
            try {
	            for(int i = 0; i < arrNotes.length(); ++i)
	            {
	            	
					jsObject = arrNotes.getJSONObject(i);
					
					String strDateTime = jsObject.getString("set_at");
	
	            	noteItem = new NoteItem();
	            	noteItem.m_nId = jsObject.getInt("id");
	            	noteItem.m_strTitle = jsObject.getString("title");
	            	noteItem.m_nType = jsObject.getInt("type");
	            	noteItem.setSetAt(strDateTime);
	            	noteItem.m_strMD5 = jsObject.getString("md5");
	            	noteItem.m_nContentSize = jsObject.getInt("content_size");
	            	noteItem.m_nLocalId = m_lstNoteItems.size();
	            	
	            	synchronized( m_lstNoteItems ) {
	            		m_lstNoteItems.add(noteItem);
	            	}
	            }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            m_nItemsLoaded = m_lstNoteItems.size();
            
            if(m_nItemsLoaded >= m_nNoteAmount)
            {
            	m_nItemsLoaded = m_nNoteAmount;
            	m_bAllItemsLoaded = true;
            }
            
            uiHandler.post( updateTask );
            
            synchronized( loading ) {
                loading = Boolean.FALSE;
                m_threadLoading = null;
            }
        }
    }

    class UIUpdateTask implements Runnable {
        public void run() {
            Log.d( LOG_TAG, "Publishing progress" );
            notifyDataSetChanged();
        }
    }

}
