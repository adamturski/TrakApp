package pl.com.turski.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.appspot.trak.shipment.Shipment;
import com.appspot.trak.shipment.model.ShipmentCreate;
import com.appspot.trak.shipment.model.ShipmentCreateRequest;
import com.appspot.trak.shipment.model.ShipmentCreateResponse;
import pl.com.turski.app.App;
import pl.com.turski.app.SettingKey;
import pl.com.turski.app.util.Util;
import pl.com.turski.trak.app.R;

import java.io.IOException;

public class CreateShipmentActivity extends Activity {

    private SharedPreferences settings;

    EditText senderCompany;
    EditText senderName;
    EditText senderSurname;
    EditText senderPhone;
    EditText senderStreet;
    EditText senderHouse;
    EditText senderFlat;
    EditText senderCity;
    EditText senderPostcode;
    EditText senderState;
    EditText senderCountry;

    EditText recipientCompany;
    EditText recipientName;
    EditText recipientSurname;
    EditText recipientPhone;
    EditText recipientStreet;
    EditText recipientHouse;
    EditText recipientFlat;
    EditText recipientCity;
    EditText recipientPostcode;
    EditText recipientState;
    EditText recipientCountry;

    Button createButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shipment);
        initView();
        settings = this.getSharedPreferences("pl.com.turski.trak.app", Context.MODE_PRIVATE);
    }

    private void initView() {
        senderCompany = (EditText) findViewById(R.id.senderCompany);
        senderName = (EditText) findViewById(R.id.senderName);
        senderSurname = (EditText) findViewById(R.id.senderSurname);
        senderPhone = (EditText) findViewById(R.id.senderPhone);
        senderStreet = (EditText) findViewById(R.id.senderStreet);
        senderHouse = (EditText) findViewById(R.id.senderHouse);
        senderFlat = (EditText) findViewById(R.id.senderFlat);
        senderCity = (EditText) findViewById(R.id.senderCity);
        senderPostcode = (EditText) findViewById(R.id.senderPostcode);
        senderState = (EditText) findViewById(R.id.senderState);
        senderCountry = (EditText) findViewById(R.id.senderCountry);

        recipientCompany = (EditText) findViewById(R.id.recipientCompany);
        recipientName = (EditText) findViewById(R.id.recipientName);
        recipientSurname = (EditText) findViewById(R.id.recipientSurname);
        recipientPhone = (EditText) findViewById(R.id.recipientPhone);
        recipientStreet = (EditText) findViewById(R.id.recipientStreet);
        recipientHouse = (EditText) findViewById(R.id.recipientHouse);
        recipientFlat = (EditText) findViewById(R.id.recipientFlat);
        recipientCity = (EditText) findViewById(R.id.recipientCity);
        recipientPostcode = (EditText) findViewById(R.id.recipientPostcode);
        recipientState = (EditText) findViewById(R.id.recipientState);
        recipientCountry = (EditText) findViewById(R.id.recipientCountry);

        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createShipment();
            }
        });
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createShipment() {
        boolean validForm = validateForm();
        if (!validForm) {
            return;
        }
        ShipmentCreate shipmentCreate = new ShipmentCreate();
        shipmentCreate.setSenderCompany(senderCompany.getText().toString());
        shipmentCreate.setSenderName(senderName.getText().toString());
        shipmentCreate.setSenderSurname(senderSurname.getText().toString());
        shipmentCreate.setSenderPhone(senderPhone.getText().toString());
        shipmentCreate.setSenderStreet(senderStreet.getText().toString());
        shipmentCreate.setSenderHouse(senderHouse.getText().toString());
        shipmentCreate.setSenderFlat(senderFlat.getText().toString());
        shipmentCreate.setSenderPostcode(senderPostcode.getText().toString());
        shipmentCreate.setSenderCity(senderCity.getText().toString());
        shipmentCreate.setSenderState(senderState.getText().toString());
        shipmentCreate.setSenderCountry(senderCountry.getText().toString());

        shipmentCreate.setRecipientCompany(recipientCompany.getText().toString());
        shipmentCreate.setRecipientName(recipientName.getText().toString());
        shipmentCreate.setRecipientSurname(recipientSurname.getText().toString());
        shipmentCreate.setRecipientPhone(recipientPhone.getText().toString());
        shipmentCreate.setRecipientStreet(recipientStreet.getText().toString());
        shipmentCreate.setRecipientHouse(recipientHouse.getText().toString());
        shipmentCreate.setRecipientFlat(recipientFlat.getText().toString());
        shipmentCreate.setRecipientPostcode(recipientPostcode.getText().toString());
        shipmentCreate.setRecipientCity(recipientCity.getText().toString());
        shipmentCreate.setRecipientState(recipientState.getText().toString());
        shipmentCreate.setRecipientCountry(recipientCountry.getText().toString());

        new CreateShipmentTask(this).execute(shipmentCreate);
    }

    private boolean validateForm() {
        int valid = 0;

        valid += Util.validateEmptyEditText(senderName);
        valid += Util.validateEmptyEditText(senderSurname);
        valid += Util.validateEmptyEditText(senderStreet);
        valid += Util.validateEmptyEditText(senderHouse);
        valid += Util.validateEmptyEditText(senderPostcode);
        valid += Util.validateEmptyEditText(senderCity);
        valid += Util.validateEmptyEditText(senderCountry);

        valid += Util.validateEmptyEditText(recipientName);
        valid += Util.validateEmptyEditText(recipientSurname);
        valid += Util.validateEmptyEditText(recipientStreet);
        valid += Util.validateEmptyEditText(recipientHouse);
        valid += Util.validateEmptyEditText(recipientPostcode);
        valid += Util.validateEmptyEditText(recipientCity);
        valid += Util.validateEmptyEditText(recipientCountry);

        return valid == 0;
    }

    private class CreateShipmentTask extends AsyncTask<ShipmentCreate, Void, Long> {

        private Context context;
        private ProgressDialog progressDialog;

        private CreateShipmentTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Rejestrowanie przesyłki na serwerze...");
            progressDialog.show();
        }

        @Override
        protected Long doInBackground(ShipmentCreate... shipmentCreates) {
            try {
                Shipment shipmentService = App.getShipmentService();
                ShipmentCreate shipmentCreate = shipmentCreates[0];
                String userId = settings.getString(SettingKey.USER_ID.getKey(), SettingKey.USER_ID.getDefValue());
                ShipmentCreateRequest shipmentCreateRequest = new ShipmentCreateRequest();
                shipmentCreateRequest.setShipmentCreate(shipmentCreate);
                shipmentCreateRequest.setUserId(Long.parseLong(userId));
                ShipmentCreateResponse shipmentCreateResponse = shipmentService.create(shipmentCreateRequest).execute();
                return shipmentCreateResponse.getShipmentId();
            } catch (IOException e) {
                Log.e(App.TAG, "IOException occured during creating shipment", e);
                CreateShipmentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateShipmentActivity.this);
                        builder.setMessage("Wystąpił błąd podczas rejestracji przesyłki")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(Long shipmentId) {
            progressDialog.dismiss();
            showWriteTagActivity(shipmentId);
        }
    }

    private void showWriteTagActivity(Long shipmentId) {
        if (shipmentId == null) {
            return;
        }

        Toast.makeText(this, "Rejestracja powiodła się. Numer przesyłki: " + shipmentId, 5000).show();
        Intent intent = new Intent(this, WriteTagActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("shipmentId", shipmentId);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
