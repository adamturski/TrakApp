package pl.com.turski.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import pl.com.turski.trak.app.R;

public class ShipmentDeliveryActivity extends Activity {

    EditText shipmentIdText;
    Button scanTagButton;
    Button saveButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipment_delivery);
        initView();
    }

    private void initView() {
        shipmentIdText = (EditText) findViewById(R.id.shipmentIdText);
        scanTagButton = (Button) findViewById(R.id.scanTagButton);
        scanTagButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanTag();
            }
        });
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shipmentDeliveryProcess();
            }
        });
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void shipmentDeliveryProcess() {
        Toast.makeText(this, "Dostarczanie przesyłki", 5000);
    }

    private void scanTag() {
        Toast.makeText(this, "Skanowanie taga", 5000);
    }
}
