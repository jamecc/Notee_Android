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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ABC
 *
 */
public class NoteeNetworkReply {
	
	private JSONObject m_jsonObject = null;
	private String m_strContentLeft = "";
    private String m_strNoteeUrlBase = "http://notee.coodesoft.com/remote/";
    private String m_strLoginFrom = "ANDROID_1_0";
    private static HttpClient m_httpClient = null;
    
    ///////////////////////
    public HttpClient getHttpClient()
    {
    	return m_httpClient;
    }
    ////////////////////////
    
	public NoteeNetworkReply()
	{
		if(m_httpClient == null)
			m_httpClient = new DefaultHttpClient();
	}
	
    public boolean login(String strName, String strPwd)
    {
        String strAction = "login";

        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("name", strName));
    	params.add(new BasicNameValuePair("pwd", strPwd));
    	params.add(new BasicNameValuePair("login_from", m_strLoginFrom));
    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return false;
    }
    
    public boolean register(String strName, String strPwd, String strEmail)
    {
        String strAction = "register";

        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("name", strName));
    	params.add(new BasicNameValuePair("pwd", strPwd));
    	params.add(new BasicNameValuePair("email", strEmail));
    	params.add(new BasicNameValuePair("login_from", m_strLoginFrom));
    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return false;
    }
    
    public boolean forgotPwd(String strUserName)
    {
        String strAction = "forgot_pwd";

        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("user_name", strUserName));
    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return false;
    }
  
    public boolean getNoteAmount(String strUserId, String strLoginToken, String strSearchText)
    {
    	String strAction = "get_amount_of_notes";
    	
        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("user_id", strUserId));
    	params.add(new BasicNameValuePair("login_token", strLoginToken));
    	if(strSearchText.length() > 0) {
    		params.add(new BasicNameValuePair("search_string", strSearchText));
    	}
    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return false;
    }
    
    public boolean getNoteListByOffset(String strUserId, String strLoginToken, String strSearchText, String strOffset, String strLimit)
    {
    	strSearchText = strSearchText.trim();
    	String strAction;
    	strAction = "get_note_list_by_offset";
    	
        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("user_id", strUserId));
    	params.add(new BasicNameValuePair("login_token", strLoginToken));
    	params.add(new BasicNameValuePair("offset", strOffset));
    	params.add(new BasicNameValuePair("number", strLimit));
    	if(strSearchText.length() > 0) {
    		params.add(new BasicNameValuePair("search_string", strSearchText));
    	}
    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return false;
    }
    
    // get note item
    public boolean getNoteItem(String strUserId, String strLoginToken, int nNoteId)
    {
    	String strAction = "get_note_item";
    	
        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("user_id", strUserId));
    	params.add(new BasicNameValuePair("login_token", strLoginToken));
    	params.add(new BasicNameValuePair("note_id", String.valueOf(nNoteId)));
    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return false;
    }
    
    // update note item
    public boolean updateNoteItem(String strUserId, String strLoginToken, 
    		NoteItem noteItem)
    {
    	String strAction = "update_note_item";
    	
        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("user_id", strUserId));
    	params.add(new BasicNameValuePair("login_token", strLoginToken));
    	params.add(new BasicNameValuePair("note_local_id", String.valueOf(noteItem.m_nLocalId)));
    	if(noteItem.m_nId >= 0)
    		params.add(new BasicNameValuePair("note_id", String.valueOf(noteItem.m_nId)));
    	params.add(new BasicNameValuePair("title", noteItem.m_strTitle));
    	params.add(new BasicNameValuePair("public_type", "0"));
    	params.add(new BasicNameValuePair("set_at", noteItem.getSetAt()));
    	params.add(new BasicNameValuePair("type", String.valueOf(noteItem.m_nType)));
    	params.add(new BasicNameValuePair("content", noteItem.m_strContent));
    	params.add(new BasicNameValuePair("content_size", String.valueOf(noteItem.m_strContent.length())));
    	params.add(new BasicNameValuePair("md5", Utils.toMd5(noteItem.m_strContent.getBytes())));
    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return false;
    }
    
    // delete note item
    public boolean deleteNoteItem(String strUserId, String strLoginToken, 
    		NoteItem noteItem)
    {
    	String strAction = "delete_note_item";
    	
        // http
        HttpPost httpPost = new HttpPost(m_strNoteeUrlBase + strAction);
        httpPost.addHeader("Connection", "Keep-Alive");
        
        // post parameter
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("user_id", strUserId));
    	params.add(new BasicNameValuePair("login_token", strLoginToken));
    	params.add(new BasicNameValuePair("note_local_id", String.valueOf(noteItem.m_nLocalId)));
   		params.add(new BasicNameValuePair("note_id", String.valueOf(noteItem.m_nId)));

    	// character set
    	HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}        

        try {
        	HttpResponse response = m_httpClient.execute(httpPost);
			
			if(parseResponse(response))
				return true;
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return false;
    }
    
    // parse response
    public boolean parseResponse(HttpResponse response)
    {
        try {
			InputStream inputStream = response.getEntity().getContent();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk = null;
            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }
            
            if(!parseJSON(stringBuilder.toString()))
            	return false;
            
            return true;
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return false;
    }
	
    // parse JSON
    public boolean parseJSON(String strBuffer)
    {
    	// from 1 to skip first '{'
    	int nPos = 1, nPosRightBracket = -1;
    	char curChar = 0;
    	boolean bInQuote = false;
    	int nInQuote2 = 0;
    	while(nPos < strBuffer.length())
    	{
    		curChar = strBuffer.charAt(nPos);
    		if(curChar ==  '"')
    		{
    			if(bInQuote)
    				bInQuote = false;
    			else
    				bInQuote = true;
    		}
    		else if(!bInQuote && curChar == '{')
    		{
    			nInQuote2++;
    		}
    		else if(curChar == '\\')
    		{
    			nPos++;
    		}
    		else if(!bInQuote && curChar == '}')
    		{
    			if(nInQuote2 == 0)
    			{
        			nPosRightBracket = nPos;
        			break;
    			}
    			nInQuote2--;
    		}
    		nPos++;
    	}

    	if(nPosRightBracket < 0)
    		return false;
    	String strJSONContent = strBuffer.substring(0, nPosRightBracket+1);
    	m_strContentLeft = strBuffer.substring(nPosRightBracket + 1);
 //   	String strJSONContent = strBuffer;
		try {
			m_jsonObject = new JSONObject(strJSONContent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
    }
    
    // get value
    public String getValue(String strName)
    {
    	if(null == m_jsonObject)
    		return "";
    	
		try {
			String str = m_jsonObject.getString(strName);
			if(str != null) {
				if(str.equalsIgnoreCase("null"))
					return "";
				return str;
			}
			return ""; 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
    public JSONObject getJSONObject(String strName)
    {
    	if(null == m_jsonObject)
    		return null;
    	
		try {
			return m_jsonObject.getJSONObject(strName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }
    
    public JSONArray getJSONArray(String strName)
    {
    	if(null == m_jsonObject)
    		return null;
    	
		try {
			return m_jsonObject.getJSONArray(strName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }
    
    public String getActionString()
    {
    	return getValue("action");
    }
    
    public String getErrorString()
    {
    	return getValue("error");
    }
    
    public String getResultString()
    {
    	return getValue("result");
    }
}

/*
byte[] buffer = getBuffer();
if(buffer.length > 0) {
   String lineEnd = "\r\n"; 
   String twoHyphens = "--"; 
   String boundary =  "RQdzAAihJq7Xp1kjraqf";
   String boundaryMiddle = twoHyphens + boundary + lineEnd;
   String boudnaryEnd = twoHyphens + boundary + twoHyphens + lineEnd;

   ByteArrayOutputStream baos = new ByteArrayOutputStream();
   DataOutputStream dos = new DataOutputStream(baos);

   // Send parameter #1
   dos.writeBytes(boundaryMiddle); 
   dos.writeBytes("Content-Disposition: form-data; name=\"param1\"" + lineEnd);
   dos.writeBytes("Content-Type: text/plain; charset=US-ASCII" + lineEnd);
   dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
   dos.writeBytes(lineEnd);
   dos.writeBytes(myStringData + lineEnd);
   dos.writeBytes(boundaryMiddle);

   // Send parameter #2
   //dos.writeBytes(twoHyphens + boundary + lineEnd); 
   //dos.writeBytes("Content-Disposition: form-data; name=\"param2\"" + lineEnd + lineEnd);
   //dos.writeBytes("foo2" + lineEnd);

   // Send a binary file
   dos.writeBytes(twoHyphens + boundary + lineEnd); 
   dos.writeBytes("Content-Disposition: form-data; name=\"param3\";filename=\"test_file.dat\"" + lineEnd); 
   dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
   dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
   dos.writeBytes(lineEnd); 
   dos.write(buffer);
   dos.writeBytes(lineEnd); 
   dos.writeBytes(boudnaryEnd); 
   dos.flush(); 
   dos.close();

   ByteArrayInputStream content = new ByteArrayInputStream(baos.toByteArray());
   BasicHttpEntity entity = new BasicHttpEntity();
   entity.setContent(content);

   HttpClient httpclient = new DefaultHttpClient();
   HttpPost httpPost = new HttpPost(myURL);
   httpPost.addHeader("Connection", "Keep-Alive");
   httpPost.addHeader("Content-Type", "multipart/form-data; boundary="+boundary);

   //MultipartEntity entity = new MultipartEntity();
   //entity.addPart("param3", new ByteArrayBody(buffer, "test_file.dat"));
   //entity.addPart("param1", new StringBody(myStringData));

   httpPost.setEntity(entity);

   /*
   String httpData = "";
   ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
   entity.writeTo(baos1);
   httpData = baos1.toString("UTF-8");
   */

   /*
   Header[] hdrs = httpPost.getAllHeaders();
   for(Header hdr: hdrs) {
     httpData += hdr.getName() + " | " + hdr.getValue() + " |_| ";
   }
   */

   //Log.e(TAG, "httpPost data: " + httpData);
   //response = httpClient.execute(httpPost);