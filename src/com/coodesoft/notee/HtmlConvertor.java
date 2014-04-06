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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.Html.ImageGetter;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

public class HtmlConvertor {
	private static MyHtmlTagHandler2 g_tagHandler;

}

class MyHtmlTagHandler2 implements ContentHandler {
	
    private static final float[] HEADER_SIZES = {
        1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };
    
	private SpannableStringBuilder m_spannableStringBuilder = new SpannableStringBuilder();
//	private TagHandler m_tagHandler = new TagHandler();
	
	private ImageGetter m_imgGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			Drawable d = null;
			try
			{
				URL aryURI = new URL(source);
				/* 打开连接 */
				URLConnection conn = aryURI.openConnection();
				conn.connect();
				/* 转变为 InputStream */
				InputStream is = conn.getInputStream();
				/* 将InputStream转变为Bitmap */
				//Bitmap bm = BitmapFactory.decodeStream(is);
				/* 关闭InputStream */
				   
				/*添加图片*/
				d = Drawable.createFromStream(is, "111");
				is.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			d.setBounds(1, 1, 45, 45);
		    return d;
		}
	 };
	 
	 
	 public MyHtmlTagHandler2() {

	 }
	 
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
        if (localName.equalsIgnoreCase("br")) {
        	handleP();
        } else if (localName.equalsIgnoreCase("p")) {
        } else if (localName.equalsIgnoreCase("div")) {
        } else if (localName.equalsIgnoreCase("em")) {
            endStyle(Bold.class, new StyleSpan(Typeface.BOLD));
        } else if (localName.equalsIgnoreCase("b")) {
        	endStyle(Bold.class, new StyleSpan(Typeface.BOLD));
        } else if (localName.equalsIgnoreCase("strong")) {
        	endStyle(Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (localName.equalsIgnoreCase("cite")) {
        	endStyle(Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (localName.equalsIgnoreCase("dfn")) {
        	endStyle(Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (localName.equalsIgnoreCase("i")) {
        	endStyle(Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (localName.equalsIgnoreCase("big")) {
        	endStyle(Big.class, new RelativeSizeSpan(1.25f));
        } else if (localName.equalsIgnoreCase("small")) {
        	endStyle(Small.class, new RelativeSizeSpan(0.8f));
        } else if (localName.equalsIgnoreCase("font")) {
            endFont();
        } else if (localName.equalsIgnoreCase("blockquote")) {
            handleP();
            endStyle(Blockquote.class, new QuoteSpan());
        } else if (localName.equalsIgnoreCase("tt")) {
        	endStyle(Monospace.class, new TypefaceSpan("monospace"));
        } else if (localName.equalsIgnoreCase("a")) {
            endA();
        } else if (localName.equalsIgnoreCase("u")) {
        	endStyle(Underline.class, new UnderlineSpan());
        } else if (localName.equalsIgnoreCase("sup")) {
        	endStyle(Super.class, new SuperscriptSpan());
        } else if (localName.equalsIgnoreCase("sub")) {
        	endStyle(Sub.class, new SubscriptSpan());
        } else if (localName.length() == 2 &&
                Character.toLowerCase(localName.charAt(0)) == 'h' &&
                		localName.charAt(1) >= '1' && localName.charAt(1) <= '6') {
            handleP();
            endHeader();
//        } else if (mTagHandler != null) {
//            mTagHandler.handleTag(false, tag, mSpannableStringBuilder, mReader);
        }		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
        if (localName.equalsIgnoreCase("br")) {
        } else if (localName.equalsIgnoreCase("p")) {
            handleP();
        } else if (localName.equalsIgnoreCase("div")) {
            handleP();
        } else if (localName.equalsIgnoreCase("em")) {
        	startStyle(new Bold());
        } else if (localName.equalsIgnoreCase("b")) {
        	startStyle(new Bold());
        } else if (localName.equalsIgnoreCase("strong")) {
        	startStyle(new Italic());
        } else if (localName.equalsIgnoreCase("cite")) {
        	startStyle(new Italic());
        } else if (localName.equalsIgnoreCase("dfn")) {
        	startStyle(new Italic());
        } else if (localName.equalsIgnoreCase("i")) {
        	startStyle(new Italic());
        } else if (localName.equalsIgnoreCase("big")) {
        	startStyle(new Big());
        } else if (localName.equalsIgnoreCase("small")) {
        	startStyle(new Small());
        } else if (localName.equalsIgnoreCase("font")) {
            startFont(atts);
        } else if (localName.equalsIgnoreCase("blockquote")) {
            handleP();
            startStyle(new Blockquote());
        } else if (localName.equalsIgnoreCase("tt")) {
        	startStyle(new Monospace());
        } else if (localName.equalsIgnoreCase("a")) {
            startA(atts);
        } else if (localName.equalsIgnoreCase("u")) {
        	startStyle(new Underline());
        } else if (localName.equalsIgnoreCase("sup")) {
        	startStyle(new Super());
        } else if (localName.equalsIgnoreCase("sub")) {
        	startStyle(new Sub());
        } else if (localName.length() == 2 &&
                   Character.toLowerCase(localName.charAt(0)) == 'h' &&
                		   localName.charAt(1) >= '1' && localName.charAt(1) <= '6') {
            handleP();
            startStyle( new Header(localName.charAt(1) - '1'));
        } else if (localName.equalsIgnoreCase("img")) {
            startImg(atts);
//        } else if (mTagHandler != null) {
//            mTagHandler.handleTag(true, tag, mSpannableStringBuilder, mReader);
        }
		
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}
	
	private void handleP() {
		m_spannableStringBuilder.append('\n');
	}
	
	private void startStyle(Object style) {
        int len = m_spannableStringBuilder.length();
        m_spannableStringBuilder.setSpan(style, len, len, Spannable.SPAN_MARK_MARK);		
	}
	
	private void endStyle(Class style, Object span) {
		int len = m_spannableStringBuilder.length();
		Object obj = getLast(style);
		int where = m_spannableStringBuilder.getSpanStart(obj);

		m_spannableStringBuilder.removeSpan(obj);

		if (where != len) {
			m_spannableStringBuilder.setSpan(span, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	private void startA(Attributes atts) {
        String href = atts.getValue("", "href");

        int len = m_spannableStringBuilder.length();
        m_spannableStringBuilder.setSpan(new Href(href), len, len, Spannable.SPAN_MARK_MARK);
	}
	
	private void endA() {
        int len = m_spannableStringBuilder.length();
        Object obj = getLast(Href.class);
        int where = m_spannableStringBuilder.getSpanStart(obj);

        m_spannableStringBuilder.removeSpan(obj);

        if (where != len) {
            Href h = (Href) obj;

            if (h.m_href != null) {
            	m_spannableStringBuilder.setSpan(new URLSpan(h.m_href), where, len,
                             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
	}
	
	private void startFont(Attributes atts) {
        String color = atts.getValue("", "color");
        String face = atts.getValue("", "face");
        String sizeS = atts.getValue("", "size");
        int size = -1;
        
        if(sizeS != null)
                size = Integer.parseInt(sizeS) * 6;

        int len = m_spannableStringBuilder.length();
        m_spannableStringBuilder.setSpan(new Font(color, face, size), len, len, Spannable.SPAN_MARK_MARK);
	}
	
	private void endFont() {
        int len = m_spannableStringBuilder.length();
        Object obj = getLast(Font.class);
        int where = m_spannableStringBuilder.getSpanStart(obj);

        m_spannableStringBuilder.removeSpan(obj);

        if (where != len) {
            Font f = (Font) obj;

            if (!TextUtils.isEmpty(f.m_color)) {
                if (f.m_color.startsWith("@")) {
                    Resources res = Resources.getSystem();
                    String name = f.m_color.substring(1);
                    int colorRes = res.getIdentifier(name, "color", "android");
                    if (colorRes != 0) {
                        ColorStateList colors = res.getColorStateList(colorRes);
                        m_spannableStringBuilder.setSpan(new TextAppearanceSpan(null, 0, 0, colors, null),
                                where, len,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    int c = getHtmlColor(f.m_color);
                    if (c != -1) {
                    	m_spannableStringBuilder.setSpan(new ForegroundColorSpan(c | 0xFF000000),
                                where, len,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            if (f.m_face != null) {
            	m_spannableStringBuilder.setSpan(new TypefaceSpan(f.m_face), where, len,
                             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            
            if (f.m_size != -1) {
            	m_spannableStringBuilder.setSpan(new AbsoluteSizeSpan(f.m_size), where, len,
                             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
	}
	
	private void startImg(Attributes atts) {
        String src = atts.getValue("", "src");
        Drawable d = null;

        if (m_imgGetter != null) {
            d = m_imgGetter.getDrawable(src);
        }

        if (d == null) {
            d = Resources.getSystem().getDrawable(android.R.drawable.picture_frame);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }

        int len = m_spannableStringBuilder.length();
        m_spannableStringBuilder.append("\uFFFC");

        m_spannableStringBuilder.setSpan(new ImageSpan(d, src), len, m_spannableStringBuilder.length(),
                     Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	
	private void endImg() {
		
	}
	
	private void endHeader() {
        int len = m_spannableStringBuilder.length();
        Object obj = getLast(Header.class);

        int where = m_spannableStringBuilder.getSpanStart(obj);

        m_spannableStringBuilder.removeSpan(obj);

        // Back off not to change only the text, not the blank line.
        while (len > where && m_spannableStringBuilder.charAt(len - 1) == '\n') {
            len--;
        }

        if (where != len) {
            Header h = (Header) obj;

            m_spannableStringBuilder.setSpan(new RelativeSizeSpan(HEADER_SIZES[h.m_level]),
                         where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            m_spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                         where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
	}
	
    private Object getLast(Class style) {
        Object[] objs = m_spannableStringBuilder.getSpans(0, m_spannableStringBuilder.length(), style);

        if (objs.length == 0) {
            return null;
        }
        
        return objs[objs.length - 1];
    }
	
    private class Bold { }
    private class Italic { }
    private class Underline { }
    private class Big { }
    private class Small { }
    private class Monospace { }
    private class Blockquote { }
    private class Super { }
    private class Sub { }

    private static class Font {
        public String m_color;
        public String m_face;
        public int m_size;

        public Font(String color, String face, int size) {
        	m_color = color;
        	m_face = face;
        	m_size = size;
        }
    }

    private static class Href {
        public String m_href;

        public Href(String href) {
        	m_href = href;
        }
    }

    private static class Header {
        private int m_level;

        public Header(int level) {
        	m_level = level;
        }
    }
    
    // colors
    private static HashMap<String,Integer> COLORS = buildColorMap();

    private static HashMap<String,Integer> buildColorMap() {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        map.put("aqua", 0x00FFFF);
        map.put("black", 0x000000);
        map.put("blue", 0x0000FF);
        map.put("fuchsia", 0xFF00FF);
        map.put("green", 0x008000);
        map.put("grey", 0x808080);
        map.put("lime", 0x00FF00);
        map.put("maroon", 0x800000);
        map.put("navy", 0x000080);
        map.put("olive", 0x808000);
        map.put("purple", 0x800080);
        map.put("red", 0xFF0000);
        map.put("silver", 0xC0C0C0);
        map.put("teal", 0x008080);
        map.put("white", 0xFFFFFF);
        map.put("yellow", 0xFFFF00);
        return map;
    }

    /**
     * Converts an HTML color (named or numeric) to an integer RGB value.
     *
     * @param color Non-null color string.
     * @return A color value, or {@code -1} if the color string could not be interpreted.
     */
    private static int getHtmlColor(String color) {
        Integer i = COLORS.get(color.toLowerCase());
        if (i != null) {
            return i;
        } else {
            try {
                return convertValueToInt(color, -1);
            } catch (NumberFormatException nfe) {
                return -1;
            }
        }
      }
    
    private static final int
    convertValueToInt(CharSequence charSeq, int defaultValue)
    {
        if (null == charSeq)
            return defaultValue;

        String nm = charSeq.toString();

        int sign = 1;
        int index = 0;
        int len = nm.length();
        int base = 10;

        if ('-' == nm.charAt(0)) {
            sign = -1;
            index++;
        }

        if ('0' == nm.charAt(index)) {
            //  Quick check for a zero by itself
            if (index == (len - 1))
                return 0;

            char    c = nm.charAt(index + 1);

            if ('x' == c || 'X' == c) {
                index += 2;
                base = 16;
            } else {
                index++;
                base = 8;
            }
        }
        else if ('#' == nm.charAt(index))
        {
            index++;
            base = 16;
        }

        return Integer.parseInt(nm.substring(index), base) * sign;
    }

	/*
	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		
        if(tag.equalsIgnoreCase("strike") || tag.equals("s")) {
            processStrike(opening, output);
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
    */
}
