package pl.com.turski.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import pl.com.turski.trak.app.R;

public class MainActivity extends Activity {

    Button createShipmentButton;
    Button writeTagButton;
    Button startShipmentDeliveryButton;
    Button endShipmentDeliveryButton;
    Button settingsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
    }

    private void initView() {
        createShipmentButton = (Button) findViewById(R.id.createShipmentButton);
        createShipmentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateShipmentActivity.class);
                startActivity(intent);
            }
        });
        writeTagButton = (Button) findViewById(R.id.writeTagButton);
        writeTagButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteTagActivity.class);
                startActivity(intent);
            }
        });
        startShipmentDeliveryButton = (Button) findViewById(R.id.startShipmentDeliveryButton);
        startShipmentDeliveryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartShipmentDeliveryActivity.class);
                startActivity(intent);
            }
        });
        endShipmentDeliveryButton = (Button) findViewById(R.id.endShipmentDeliveryButton);
        endShipmentDeliveryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EndShipmentDeliveryActivity.class);
                startActivity(intent);
            }
        });
        settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
