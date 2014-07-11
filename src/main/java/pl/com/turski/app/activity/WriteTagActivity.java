package pl.com.turski.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import pl.com.turski.app.App;
import pl.com.turski.app.model.WriteTagResponse;
import pl.com.turski.app.util.Util;
import pl.com.turski.trak.app.R;

import java.io.IOException;
import java.nio.charset.Charset;

public class WriteTagActivity extends Activity {

    private NfcAdapter nfcAdapter;
    private boolean canWrite;
    private Long shipmentId;

    AlertDialog approachTagDialog;

    EditText shipmentIdText;
    Button saveButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_tag);
        initView();
        checkNfc();
    }

    private void initView() {
        shipmentIdText = (EditText) findViewById(R.id.shipmentIdText);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveButtonAction();
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
            if (shipmentId != 0) {
                shipmentIdText.setText(shipmentId.toString());
                shipmentIdText.setEnabled(false);
                this.shipmentId = shipmentId;
            }
        }
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

    private void saveButtonAction() {
        if (shipmentId == null) {
            int valid = Util.validateEmptyEditText(shipmentIdText);
            if (valid == 0) {
                String shipmentIdString = shipmentIdText.getText().toString().trim();
                shipmentId = Long.parseLong(shipmentIdString);
            } else {
                return;
            }
        }

        checkNfc();
        if (nfcAdapter == null) {
            return;
        }

        canWrite = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Zbliż tag")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        canWrite = false;
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
            if (canWrite) {
                String type = intent.getType();
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (App.MIME_TEXT_PLAIN.equals(type)) {
                    if (writableTag(tag)) {
                        //writeTag here
                        WriteTagResponse wr = writeTag(createMessage(), tag);
                        String message = (wr.getStatus() == 1 ? "Ok: " : "Błąd: ") + wr.getMessage();
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        if (wr.getStatus() == 1) {
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Tag nie umożliwia zapisu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(App.TAG, "Niewspierany typ tagu: " + type);
                    Toast.makeText(this, "Niewspierany typ tagu: " + type, Toast.LENGTH_SHORT).show();
                }
                approachTagDialog.dismiss();
            }
        }
    }

    private NdefRecord createRecord(String text) {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes(Charset.forName("UTF-8"));
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }


    private NdefMessage createMessage() {
        NdefRecord[] records = {createRecord(shipmentId.toString())};
        return new NdefMessage(records);
    }

    public WriteTagResponse writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return new WriteTagResponse(0, "Tag tylko do odczytu");
                }
                if (ndef.getMaxSize() < size) {
                    return new WriteTagResponse(0, "Pojemność tagu " + ndef.getMaxSize() + " bajtów, rozmiar danych " + size + " bajtów");
                }
                ndef.writeNdefMessage(message);
                return new WriteTagResponse(1, "Pomyślnie zapisano dane na tagu");
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return new WriteTagResponse(1, "Pomyślnie sformatowano i zapisano dane na tagu");
                    } catch (IOException e) {
                        return new WriteTagResponse(0, "Formatowanie tagu nieudane");
                    }
                } else {
                    return new WriteTagResponse(0, "Tag nie wspiera technologii NDEF");
                }
            }
        } catch (Exception e) {
            return new WriteTagResponse(0, "Zapis na tagu nieudany");
        }
    }

    private boolean writableTag(Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag tylko do odczytu", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return false;
                }
                ndef.close();
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Błąd odczytu taga", Toast.LENGTH_SHORT).show();
        }
        return false;
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

        // Notice that this is the same filter as in our manifest.
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
}
