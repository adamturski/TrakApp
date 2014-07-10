package pl.com.turski.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import pl.com.turski.trak.app.R;

public class WriteTagActivity extends Activity {

    EditText shipmentIdText;
    Button saveButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_tag);
        initView();
    }

    private void initView() {
        shipmentIdText = (EditText) findViewById(R.id.shipmentIdText);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                writeTag();
            }
        });
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Long shipmentId = bundle.getLong("shipmentId");
            shipmentIdText.setText(shipmentId.toString());
        }
    }

    private void writeTag() {
        Toast.makeText(this, "Zapisywanie taga", 5000);
    }
}
