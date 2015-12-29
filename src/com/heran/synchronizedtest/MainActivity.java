package com.heran.synchronizedtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Synchronized mSynchronized = new Synchronized();

	     Thread thread1 = new Thread(new Runnable() {
	          @Override
	          public void run() {
	               mSynchronized.method0();
	               // mSynchronized.method1();
	               // new Synchronized().method1();
	               // new Synchronized().method2();
	               // mSynchronized.method1();
	          }
	     },"A");


	     Thread thread2 = new Thread(new Runnable() {
	          @Override
	          public void run() {
	               mSynchronized.method0();
	               // mSynchronized.method1();
	               // new Synchronized().method1();
	               // new Synchronized().method2();
	               // mSynchronized.method3();
	          }
	     }, "B");

	     Log.d(Synchronized.TAG, "Start Thread!");
	     thread1.start();
	     thread2.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
