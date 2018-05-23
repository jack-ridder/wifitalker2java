package com.wifitalk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wifitalk.phone.NetPhone;

public class WifiTalk extends Activity {

	private Button connectB;
	private Button stopB;
	private EditText ipText;

	private NetPhone phone;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		connectB = (Button) findViewById(R.id.connectB);
		stopB = (Button) findViewById(R.id.stop);
		ipText = (EditText) findViewById(R.id.ipText);

		connectB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String ip = ipText.getText().toString();
				phone = new NetPhone();
				try {
					phone.startPhone(ip);
					ipText.setEnabled(false);
					Toast.makeText(WifiTalk.this, "start phone ok!", 1000);
				} catch (Exception e) {
					Log.e(WifiTalk.class.getName(), e.getMessage(), e);
				}
			}
		});

		stopB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phone != null)
					try {
						phone.stopPhone();
						ipText.setEnabled(true);
						Toast.makeText(WifiTalk.this, "stop phone ok!", 1000);
					} catch (Exception e) {
						Log.e(WifiTalk.class.getName(), e.getMessage(), e);
					}
			}
		});

	}

	@Override
	protected void onDestroy() {
		try {
			phone.stopPhone();
		} catch (Exception e) {
			Log.e("phone", e.getMessage(), e);
		}
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}