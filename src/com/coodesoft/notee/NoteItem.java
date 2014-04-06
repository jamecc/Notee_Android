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
import java.util.Date;

public class NoteItem {
	
	public int m_nId = -1;
	public int m_nLocalId = -1;
	public int m_nType = 1; // 1 note, 0 schedule
	public String m_strTitle = new String();
	public String m_strContent = new String();
	public int m_nContentSize = 0;
	public String m_strMD5 = new String();
	public Date m_dtSetAt = new Date();
	public Date m_dtUpdatedAt = new Date();
	public Date m_dtCreatedAt = new Date();
	public int m_nUserId = -1;
	public static final int TYPE_NOTE = 1;
	public static final int TYPE_SCHEDULE = 2;	
	
	public NoteItem() {
		m_strTitle = new String();
	}
	
	public void setSetAt(String strDateTime) {
		if(isNote()) 
		{
			m_dtSetAt = Utils.parseDate2Date(strDateTime);
			return;
		}
		
		m_dtSetAt = Utils.parseDateTime2Date(strDateTime);
	}
	
	public void setUpdatedAt(String strDateTime) {
		m_dtUpdatedAt = Utils.parseDateTime2Date(strDateTime);
	}
	
	public void setCreatedAt(String strDateTime) {
		m_dtCreatedAt = Utils.parseDateTime2Date(strDateTime);
	}
	
	public String getSetAt() 
	{
		if(isNote())
		{
			return Utils.toDate(m_dtSetAt);
		}

		return Utils.toDateTime(m_dtSetAt);
	}
	
	public String getSetAtDate() {
		return Utils.toDate(m_dtSetAt);
	}
	
	public String getSetAtTime() {
		if(isNote())
			return "";
		
		return Utils.toTime(m_dtSetAt);
	}
	
	public String getUpdatedAt() {
		return Utils.toDateTime(m_dtUpdatedAt);
	}
	
	public String getCreatedAt() {
		return Utils.toDateTime(m_dtCreatedAt);
	}
	
	public boolean isNote() {
		return m_nType == TYPE_NOTE;
	}
	
	public ArrayList<AttachmentItem> m_lstAttachments = new ArrayList<AttachmentItem>();
}
