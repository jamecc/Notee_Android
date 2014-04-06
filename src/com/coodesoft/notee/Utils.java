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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.widget.TextView;

public class Utils {
	public static boolean m_bAutoLogin = false;
	public static String m_strUserName = "";
	public static String m_strPassword = "";
	public static final String m_strUrlNotee = "http://notee.coodesoft.com";
	
	private static final String SETTING_NOTEE = "notee.coodesoft.com";
	private static final String SETTING_AUTO_LOGIN = "auto_login";
	private static final String SETTING_USER_NAME = "user_name";
	private static final String SETTING_PASSWORD = "user_password";
	
	public static void loadSettings(Activity activity) {
        // load setting
        SharedPreferences settings = activity.getSharedPreferences(SETTING_NOTEE, 0);
        m_bAutoLogin = settings.getBoolean(SETTING_AUTO_LOGIN, false);
        m_strUserName = settings.getString(SETTING_USER_NAME, "");
        m_strPassword = settings.getString(SETTING_PASSWORD, "");
    }
    
	public static void saveSettings(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(SETTING_NOTEE, 0);
       
        Editor editor = settings.edit();
        editor.putBoolean(SETTING_AUTO_LOGIN, m_bAutoLogin);
        if(m_bAutoLogin)
        {
            editor.putString(SETTING_USER_NAME, m_strUserName);
            editor.putString(SETTING_PASSWORD, m_strPassword);
        }
        else
        {
            editor.putString(SETTING_USER_NAME, "");
            editor.putString(SETTING_PASSWORD, "");
        }

        editor.commit();
    }
	
	// make read only
	public static void makeReadOnly(TextView editText, int nClr)
	{
		editText.setOnTouchListener(null);
		editText.setOnKeyListener(null);
		editText.setOnClickListener(null);
		
		editText.setFocusable(false);
		editText.setFocusableInTouchMode(false);
		editText.setCursorVisible(false);		
		
		if(nClr >= 0)
			editText.setTextColor(ColorStateList.valueOf(nClr));
	}
	
	// parse date time
	public static Date parseDateTime2Date(String strDateTime)
	{
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

		Date dt = null;

		try {
			dt = format.parse(strDateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}
	
	// parse date time
	public static Date parseDate2Date(String strDate)
	{
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

		Date dt = null;

		try {
			dt = format.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}
	
	public static Date parseTime2Time(String strTime)
	{
		SimpleDateFormat  format = new SimpleDateFormat("HH:mm", Locale.US);

		Date dt = null;

		try {
			dt = format.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}
	
	public static String toDateTime(Date dt) {
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		return format.format(dt);
	}
	
	public static String toDate(Date dt) {
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return format.format(dt);
	}
	
	public static String toTime(Date dt) {
		SimpleDateFormat  format = new SimpleDateFormat("HH:mm", Locale.US);
		return format.format(dt);
	}
	
	// get date time string
	public static String getDateTime2Date(String strDateTime)
	{
		String dt = "";
		int nPos = strDateTime.indexOf(' ');
		if(nPos < 0)
		{
			dt = strDateTime;
		}
		else
		{
			dt = strDateTime.substring(0, nPos - 1);
		}		

		return dt;
	}
	
	public static String getDateTime2Time(String strDateTime)
	{
		String tm = "";
		int nPos = strDateTime.indexOf(' ');
		if(nPos > 0)
		{
			tm = strDateTime.substring(nPos + 1);
		}		

		return tm;
	}
	
	// md5
	public static String toMd5(byte[] bytes) {
        try {
                MessageDigest algorithm = MessageDigest.getInstance("MD5");
                algorithm.reset();
                algorithm.update(bytes);
                return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
                // 05-20 09:42:13.697: ERROR/hjhjh(256):
                // 5d5c87e61211ab7a4847f7408f48ac
        }
	}
	
	private static String toHexString(byte[] bytes, String separator) {
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : bytes) {
	                hexString.append(Integer.toHexString(0xFF & b)).append(separator);
	        }
	        return hexString.toString();
	}
}
