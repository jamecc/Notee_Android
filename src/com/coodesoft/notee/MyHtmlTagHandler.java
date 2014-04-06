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

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.util.Base64;


public class MyHtmlTagHandler implements TagHandler {
	private boolean m_bIncludeSytle = false;

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		
        if(tag.equalsIgnoreCase("strike") || tag.equals("s")) {
            processStrike(opening, output);
        } else if (tag.equalsIgnoreCase("style")) {
        	processStyle(opening, output);
        } else if (tag.equalsIgnoreCase("br")) {
        	processP(opening, output);
        } else if (tag.equalsIgnoreCase("p")) {
        	processP(opening, output);
        } else if (tag.equalsIgnoreCase("div")) {
        	processP(opening, output);
        } else if (tag.equalsIgnoreCase("em")) {
        } else if (tag.equalsIgnoreCase("b")) {
        } else if (tag.equalsIgnoreCase("strong")) {
        } else if (tag.equalsIgnoreCase("cite")) {
        } else if (tag.equalsIgnoreCase("dfn")) {
        } else if (tag.equalsIgnoreCase("i")) {
        } else if (tag.equalsIgnoreCase("big")) {
        } else if (tag.equalsIgnoreCase("small")) {
        } else if (tag.equalsIgnoreCase("font")) {
        } else if (tag.equalsIgnoreCase("blockquote")) {
        } else if (tag.equalsIgnoreCase("tt")) {
        } else if (tag.equalsIgnoreCase("a")) {
        	processA(opening, output, xmlReader);
        } else if (tag.equalsIgnoreCase("u")) {
        } else if (tag.equalsIgnoreCase("sup")) {
        } else if (tag.equalsIgnoreCase("sub")) {
        }
	}
	
	private void processA(boolean opening, Editable output, XMLReader xmlReader) {
        int len = output.length();
        if(opening) {
            output.setSpan(new TagA(), len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Object obj = getLast(output, TagA.class);
            int where = output.getSpanStart(obj);

            output.removeSpan(obj);
            
			try {
				String strHref = (String) xmlReader.getProperty("href");
				
	            if (where != len) {
	                output.setSpan(new URLSpan(strHref), where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
			} catch (SAXNotRecognizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
    private void processStrike(boolean opening, Editable output) {
        int len = output.length();
        if(opening) {
            output.setSpan(new StrikethroughSpan(), len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Object obj = getLast(output, StrikethroughSpan.class);
            int where = output.getSpanStart(obj);

            output.removeSpan(obj);

            if (where != len) {
                output.setSpan(new StrikethroughSpan(), where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
    
    private void processStyle(boolean opening, Editable output) {
        int len = output.length();
        if(opening) {
        	m_bIncludeSytle = true;
        	output.setSpan(new TagStyle(), len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Object obj = getLast(output, TagStyle.class);
            int where = output.getSpanStart(obj);
            output.removeSpan(obj);
            
        	output.delete(where, len - where);
        }
    }

    private void processP(boolean opening, Editable output) {
        int len = output.length();
        if(opening) {
        	output.setSpan(new TagP(), len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Object obj = getLast(output, TagP.class);
            int where = output.getSpanStart(obj);
            output.removeSpan(obj);

        	if(m_bIncludeSytle) {
        		output.delete(where, len - where);
        	}        	
        }
        
        m_bIncludeSytle = false;
    }
    
    private Object getLast(Editable text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            for(int i = objs.length;i>0;i--) {
                if(text.getSpanFlags(objs[i-1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i-1];
                }
            }
            return null;
        }
    }
    
    private static class TagA { }
    private static class TagP { }
    private static class TagStyle { }    

}
