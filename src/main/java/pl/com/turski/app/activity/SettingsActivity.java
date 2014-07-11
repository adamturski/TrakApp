package pl.com.turski.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import pl.com.turski.app.SettingKey;
import pl.com.turski.trak.app.R;

public class SettingsActivity extends Activity {

    TextView serverUrlText;
    TextView userIdText;
    Button saveButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initView();
    }

    private void initView() {
        SharedPreferences settings = this.getSharedPreferences("pl.com.turski.trak.app", Context.MODE_PRIVATE);
        serverUrlText = (TextView) findViewById(R.id.serverUrlText);
        serverUrlText.setText(settings.getString(SettingKey.SERVER_URL.getKey(), SettingKey.SERVER_URL.getDefValue()));
        userIdText = (TextView) findViewById(R.id.userIdText);
        userIdText.setText(settings.getString(SettingKey.USER_ID.getKey(), SettingKey.USER_ID.getDefValue()));
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveSettings();
                finish();
            }
        });
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveSettings() {
        SharedPreferences settings = this.getSharedPreferences("pl.com.turski.trak.app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SettingKey.SERVER_URL.getKey(), serverUrlText.getText().toString());
        editor.putString(SettingKey.USER_ID.getKey(), userIdText.getText().toString());
        editor.commit();
    }
}
