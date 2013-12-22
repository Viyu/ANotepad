package com.android.anotepad.viyu.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;

import com.android.anotepad.viyu.utility.Constant;

public class NoteXmlHandler {
	
	private static NoteXmlHandler instance = null;
	
	private File notesDir = null; 	

	private NoteXmlHandler(Context context) {
		setContext(context);
	}
	
	public static NoteXmlHandler getInstance(Context context) {
		if(instance == null) {
			instance = new NoteXmlHandler(context);
		}
		instance.setContext(context);
		return instance;
	}
	
	private void setContext(Context context) {
		notesDir = context.getDir(Constant.NOTE_LIST_XML_DIR, Context.MODE_PRIVATE);
	}
	
	private File getFile(String fileName) {
		return new File(notesDir, fileName);
	}
	
	public boolean saveNote(NoteObject<String, String> noteObj) {
		FileOutputStream fileOutputStream = null;
		
		try {
			File noteFile = getFile(noteObj.getFileName());
			if(noteFile.exists()) {
				noteFile.delete();
			}
			fileOutputStream = new FileOutputStream(noteFile);
			
			XmlSerializer xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput(fileOutputStream, "utf-8");
			//set header
			xmlSerializer.startDocument("utf-8", null);
			xmlSerializer.startTag(null, Constant.NOTE_XML_TAG_NOTE);
			
			//set category attribute
			xmlSerializer.attribute(null, Constant.NOTE_XML_ATTRIBUTE_CATEGORY, "" + noteObj.getTextColor().convertToKey());
			xmlSerializer.attribute(null, Constant.NOTE_XML_ATTRIBUTE_ALARM, "" + noteObj.getAlarm().convertToMessage());
			
			//set content
			xmlSerializer.text(noteObj.getContent());
			xmlSerializer.endTag(null, Constant.NOTE_XML_TAG_NOTE);
			xmlSerializer.endDocument();
			fileOutputStream.close();
			fileOutputStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	public boolean removeNote(NoteObject<String, String> noteObj) {
		return getFile(noteObj.getFileName()).delete();
	}

	public List<NoteObject<String, String>> loadNoteList() {
		List<NoteObject<String, String>> list = new ArrayList<NoteObject<String, String>>();
		String[] noteFiles = notesDir.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.toLowerCase(Locale.getDefault()).endsWith(Constant.NOTE_XML_FILE_SUFFIX);
			}
		});
		
		if(noteFiles != null && noteFiles.length > 0) {
			for(String note: noteFiles) {
				list.add(parseNote(note));
			}
		}
		//Sort by last modified time
		Collections.sort(list);
		return list;
	}
	
	private NoteObject<String, String> parseNote(String fileName) {
		NoteObject<String, String> noteObj = null;
		
		File file = getFile(fileName);
		String noteContent = null;
		int noteColorKey = Constant.KEY_TEXT_COLOR_WHITE;
		String alarmMessage = Constant.MESSAGE_ALARM_NOALARM;
		
		try {
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(new FileInputStream(file), "utf-8");
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG: {
					String name = xmlPullParser.getName();
					if (name.equals(Constant.NOTE_XML_TAG_NOTE)) {
						try { 
							noteColorKey = Integer.parseInt(xmlPullParser.getAttributeValue(null, Constant.NOTE_XML_ATTRIBUTE_CATEGORY));
							alarmMessage = xmlPullParser.getAttributeValue(null, Constant.NOTE_XML_ATTRIBUTE_ALARM);
						}catch(Exception e) {
							e.printStackTrace();
						}
					} 
					break;
				}
				case XmlPullParser.TEXT: {
					noteContent = xmlPullParser.getText();
					break;
				}
				default:
						break;

				}
				eventType = xmlPullParser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		noteObj = new NoteObject<String, String>(fileName, noteContent, AlarmObject.convertToAlarm(alarmMessage),
				ColorSeries.convertToTextColor(noteColorKey), file.lastModified());
		return noteObj;
	}
}