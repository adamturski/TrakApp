package pl.com.turski.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.*;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.appspot.trak.shipment.Shipment;
import com.appspot.trak.shipment.model.EndShipmentRequest;
import pl.com.turski.app.App;
import pl.com.turski.app.settings.SettingKey;
import pl.com.turski.trak.app.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class EndShipmentDeliveryActivity extends Activity {

    private SharedPreferences settings;
    private AlertDialog approachTagDialog;
    private NfcAdapter nfcAdapter;
    private boolean canRead;

    Button acceptButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_shipment_delivery);
        initView();
        checkNfc();
        settings = this.getSharedPreferences("pl.com.turski.trak.app", Context.MODE_PRIVATE);
    }

    private void initView() {
        acceptButton = (Button) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                endShipmentDeliveryProcess();
            }
        });
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkNfc() {
        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        nfcAdapter = nfcManager.getDefaultAdapter();
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Włącz obsługę NFC i spróbuj ponownie")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
            finish();
        }
    }

    private void endShipmentDeliveryProcess() {
        checkNfc();
        if (nfcAdapter == null) {
            return;
        }

        canRead = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Zbliż tag")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        canRead = false;
                        dialog.dismiss();
                    }
                });
        approachTagDialog = builder.create();
        approachTagDialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            if (canRead) {
                String type = intent.getType();
                if (App.MIME_TEXT_PLAIN.equals(type)) {

                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    new EndShipmentDeliveryTask(this).execute(tag);

                } else {
                    Log.d(App.TAG, "Niewspierany typ tagu: " + type);
                    Toast.makeText(this, "Niewspierany typ tagu: " + type, Toast.LENGTH_SHORT).show();
                }
                approachTagDialog.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, nfcAdapter);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(App.MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private class EndShipmentDeliveryTask extends AsyncTask<Tag, Void, Boolean> {

        private Context context;
        private ProgressDialog progressDialog;

        private EndShipmentDeliveryTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Aktualizacja statusu przesyłki...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Tag... tags) {
            try {
                Shipment shipmentService = App.getShipmentService();
                Tag tag = tags[0];
                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {
                    // NDEF is not supported by this Tag.
                    return null;
                }

                NdefMessage ndefMessage = ndef.getCachedNdefMessage();

                NdefRecord[] records = ndefMessage.getRecords();
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        try {
                            String shipmentId = readText(ndefRecord);
                            String userId = settings.getString(SettingKey.USER_ID.getKey(), SettingKey.USER_ID.getDefValue());
                            EndShipmentRequest endShipmentRequest = new EndShipmentRequest();
                            endShipmentRequest.setShipmentId(Long.parseLong(shipmentId));
                            endShipmentRequest.setUserId(Long.parseLong(userId));
                            shipmentService.endShipmentDelivery(endShipmentRequest).execute();
                            return Boolean.TRUE;
                        } catch (UnsupportedEncodingException e) {
                            Log.e(App.TAG, "Unsupported Encoding", e);
                        }
                    }
                }
                return Boolean.FALSE;
            } catch (IOException e) {
                Log.e(App.TAG, "IOException occured during creating shipment", e);
                EndShipmentDeliveryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EndShipmentDeliveryActivity.this);
                        builder.setMessage("Wystąpił błąd podczas aktualizacji statusu przesyłki")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                });
                return Boolean.FALSE;
            }
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result) {
                EndShipmentDeliveryActivity.this.finish();
            }
        }
    }
}
