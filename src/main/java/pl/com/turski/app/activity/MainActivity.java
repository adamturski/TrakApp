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
    Button shipmentDeliveryButton;
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
        shipmentDeliveryButton = (Button) findViewById(R.id.shipmentDeliveryButton);
        shipmentDeliveryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShipmentDeliveryActivity.class);
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
