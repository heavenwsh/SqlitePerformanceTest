package com.example.sqliteperformancetest;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.ContentValues;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "MainActivity";
	private final int COUNT = 100000;
	
	TextView message;
	Button start, startWithHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findView();
		setViewListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		message = (TextView)findViewById(R.id.message);
		start = (Button)findViewById(R.id.start);
		startWithHelper = (Button)findViewById(R.id.start_with_helper);
	}
	
	private void setViewListener() {
		// TODO Auto-generated method stub
		start.setOnClickListener(this);
		startWithHelper.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.start:
			createToTestData();
			break;
		case R.id.start_with_helper:
			createToTestDataInHelper();
			break;
		}
		
	}
	
	int index1;
	int index2;
	int index3;
	int index4;
	int index5;

	private void createToTestDataInHelper() {
		// TODO Auto-generated method stub
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d(TAG, "createToTestDataInHelper start to save data +++++++");
				DatabaseHandler dbHelper = new DatabaseHandler(MainActivity.this);
				long start = System.currentTimeMillis();
				SQLiteDatabase db = dbHelper.getWritableDB();
				InsertHelper ih = new InsertHelper(db, dbHelper.getTableName());
				index1 = ih.getColumnIndex("name");
				index2 = ih.getColumnIndex("phone_number");
				index3 = ih.getColumnIndex("key1");
				index4 = ih.getColumnIndex("key2");
				index5 = ih.getColumnIndex("key3");
//				db.execSQL("PRAGMA synchronous=OFF");
//				db.setLockingEnabled(false);
				db.beginTransaction();
				try{
					for(int i = 0; i < COUNT; i ++) {
						ih.prepareForInsert();
						addTestData(ih);
						ih.execute();
						handler.sendEmptyMessage(i + 1);
					}
					db.setTransactionSuccessful();
				}finally {
					
					db.endTransaction();
//					db.execSQL("PRAGMA synchronous=NORMAL");
					db.close();
					ih.close();
//					db.setLockingEnabled(true);
					long end = System.currentTimeMillis();
					Log.d(TAG, "createToTestDataInHelper end to save data -------" + "[" + (end -start) + "]");
				}
			}
			
		}.start();
	}

	protected void addTestData(InsertHelper ih) {
		// TODO Auto-generated method stub
		
		ih.bind(index1, "name " + id);
		ih.bind(index2, "phone_number " + id);
		ih.bind(index3, "key1 " + id);
		ih.bind(index4, "key2 " + id);
		ih.bind(index5, "key3 " + id ++);
	}

	private void createToTestData() {
		// TODO Auto-generated method stub
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d(TAG, "start to save data +++++++");
				DatabaseHandler dbHelper = new DatabaseHandler(MainActivity.this);
				long start = System.currentTimeMillis();
				SQLiteDatabase db = dbHelper.getWritableDB();
				db.beginTransaction();
				for(int i = 0; i < COUNT; i ++) {
					ContentValues data = getTestData();
					dbHelper.addData(data, db);
					handler.sendEmptyMessage(i + 1);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
				db.close();
				long end = System.currentTimeMillis();
				Log.d(TAG, "end to save data -------" + "[" + (end -start) + "]");
			}
			
		}.start();
	}
	
	public static int id = 0;
	
	protected ContentValues getTestData() {
		// TODO Auto-generated method stub
		ContentValues values = new ContentValues();
		values.put("name", "name " + id);
		values.put("phone_number", "phone_number " + id);
		values.put("key1", "key1 " + id);
		values.put("key2", "key2 " + id);
		values.put("key3", "key3 " + id);
		id ++;
		return values;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			message.setText(msg.what + "/" + COUNT);
		}
		
	};
}
