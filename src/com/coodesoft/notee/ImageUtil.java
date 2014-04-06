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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class ImageUtil {
	/**
     * 通过路径获取输入流
     * 
     * @param path
     *            路径
     * @return 输入流
     * @throws Exception
     *             异常
     */ 
    public static InputStream getRequest(String path) throws Exception { 
        URL url = new URL(path); 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
        conn.setRequestMethod("GET"); 
        conn.setConnectTimeout(5000); 
        if (conn.getResponseCode() == 200) { 
            return conn.getInputStream(); 
        } 
        return null; 
    } 
 
    /**
     * 从流中读取二进制数据
     * 
     * @param inStream
     *            输入流
     * @return 二进制数据
     * @throws Exception
     *             异常
     */ 
    public static byte[] readInputStream(InputStream inStream) throws Exception { 
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream(); 
        byte[] buffer = new byte[4096]; 
        int len = 0; 
        while ((len = inStream.read(buffer)) != -1) { 
            outSteam.write(buffer, 0, len); 
        } 
        outSteam.close(); 
        inStream.close(); 
        
        return outSteam.toByteArray(); 
    } 
 
    /**
     * 把一个路径转换成Drawable对象
     * 
     * @param url
     *            路径
     * @return Drawable对象
     */ 
    public static Drawable loadImageFromUrl(String url) { 
        URL m; 
        InputStream i = null; 
        try { 
            m = new URL(url); 
            i = (InputStream) m.getContent(); 
        } catch (MalformedURLException e1) { 
            e1.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
        Drawable d = Drawable.createFromStream(i, "src"); 
        return d; 
    } 
 
    /**
     * 把一个路径转换成Drawable对象
     * 
     * @param url
     *            字符串路径
     * @return Drawable对象
     * @throws Exception
     *             异常
     */ 
    public static Drawable getDrawableFromUrl(String url) throws Exception { 
        return Drawable.createFromStream(getRequest(url), null); 
    } 
}
