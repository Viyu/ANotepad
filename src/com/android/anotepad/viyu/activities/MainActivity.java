package com.android.anotepad.viyu.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.anotepad.viyu.R;
import com.android.anotepad.viyu.components.NoteListAdapter;
import com.android.anotepad.viyu.data.AlarmObject;
import com.android.anotepad.viyu.data.NoteObject;
import com.android.anotepad.viyu.data.NoteXmlHandler;
import com.android.anotepad.viyu.receivers.AlarmReceiver;
import com.android.anotepad.viyu.utility.Constant;

public class MainActivity extends Activity implements OnItemClickListener {
	
	private List<NoteObject<String, String>> noteList = null;
	private NoteListAdapter noteListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//load the existing notes
		ListView noteList = (ListView)findViewById(R.id.NoteListViewId);
		
		noteListAdapter = new NoteListAdapter(this, loadNoteList(), R.layout.note_list_layout, 
				new String[]{Constant.NOTE_LIST_COLUMN_CONTENT, Constant.NOTE_LIST_COLUMN_LASTMODIFIED}, 
				new int[]{R.id.note_brief, R.id.note_modified_time});
		
		noteList.setAdapter(noteListAdapter);
		
		noteList.setOnItemClickListener(this);
		this.registerForContextMenu(noteList);
	}
	
	/**
	 * @param fileName
	 * @param content
	 * @param categoryValue Reserved for now, the value is -1.
	 */
	private void toEditTextNote(String fileName, String content, String alarmMessage, int textColorKey) {
		Intent intent = new Intent(this, EditNoteActivity.class);       
        intent.putExtra(Constant.INTENT_EXTRA_FILE_NAME, fileName);
        intent.putExtra(Constant.INTENT_EXTRA_CONTENT, content);
        intent.putExtra(Constant.INTENT_EXTRA_ALARMK_MESSAGE, alarmMessage);
        intent.putExtra(Constant.INTENT_EXTRA_TEXTCOLOR_KEY, textColorKey);
        startActivity(intent);  
	}
	
	private List<NoteObject<String, String>> loadNoteList() {
		if(noteList != null) {
			noteList.clear();
			noteList = null;
		}
		return noteList = NoteXmlHandler.getInstance(this).loadNoteList();
	}
	
	public void createNewTextNoteButtonClicked(View view) {
		String name = System.currentTimeMillis() + Constant.NOTE_XML_FILE_SUFFIX;
		toEditTextNote(name, null, Constant.MESSAGE_ALARM_NOALARM, Constant.KEY_TEXT_COLOR_WHITE);
	}
	
	public void createNewVoiceNoteButtonClicked(View view) {
		//TODO
	}
	
	public void createNewPhotoButtonClicked(View view) {
		//TODO
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		NoteObject<String, String> noteObj = noteList.get(position);
		toEditTextNote(noteObj.getFileName(), noteObj.getContent(), noteObj.getAlarm().convertToMessage(), noteObj.getTextColor().convertToKey());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(R.string.menu_note_listview_header_title);  
		menu.add(Menu.NONE, Constant.ID_NOTE_LISTVIEW_CONTEXTMENU_DELETE, Menu.NONE, R.string.menu_note_listview_delete);		
		menu.add(Menu.NONE, Constant.ID_NOTE_LISTVIEW_CONTEXTMENU_SEND, Menu.NONE, R.string.menu_note_listview_send);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ContextMenuInfo info = item.getMenuInfo();
		if(info instanceof AdapterContextMenuInfo) {
			AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo)info;
			NoteObject<String, String> noteObj = this.noteList.get(adapterInfo.position);

			switch(item.getItemId()) {
				case Constant.ID_NOTE_LISTVIEW_CONTEXTMENU_DELETE: {
					if(!noteObj.getAlarm().isNull()) {//Remove alarm
						Intent intent = new Intent(this, AlarmReceiver.class);
						PendingIntent pendingIntent = PendingIntent.getBroadcast(this, noteObj.getAlarmCode(), intent, PendingIntent.FLAG_NO_CREATE);
						if(pendingIntent != null) {
							AlarmManager amarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
							amarmManager.cancel(pendingIntent);
							pendingIntent.cancel();
							noteObj.setAlarm(AlarmObject.convertToAlarm(Constant.MESSAGE_ALARM_NOALARM));
						}
					}
					
					//remove file
					boolean flag = NoteXmlHandler.getInstance(this).removeNote(noteObj);
					
					noteList.remove(adapterInfo.position);
					noteListAdapter.notifyDataSetChanged();
					
					return flag;
				}
				case Constant.ID_NOTE_LISTVIEW_CONTEXTMENU_SEND: {
					Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
					intent.putExtra("sms_body", noteObj.getContent());
					startActivity(intent);
				}
					break;
				default:
			}
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_about: {
				Intent intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
				break;
			}
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
