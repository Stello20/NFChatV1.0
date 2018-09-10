package za.co.castellogovender.android.nfchat;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import za.co.castellogovender.android.nfchat.NfcHelper;
import za.co.castellogovender.android.nfchat.R;

public class KeyExchange extends AppCompatActivity implements
        NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private NfcHelper nfcHelper;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_exchange);

        nfcHelper = new NfcHelper(this);

        handler = new Handler() {
            @Override
            public  void handleMessage(Message msg) {

                Toast.makeText(getApplicationContext(), "Key sent!", Toast.LENGTH_LONG).show();

            }
        };

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        nfcAdapter.setNdefPushMessageCallback(this, this);
        nfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String content = ((TextView) findViewById(R.id.txt_Key)).getText().toString();

        return new NdefMessage( new NdefRecord[]{ nfcHelper.createTextRecord(content) });

    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        handler.obtainMessage(1).sendToTarget();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (!nfcHelper.isNfcIntent(intent)) {

            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

                    if (sharedText != null) {
                        ((TextView) findViewById(R.id.txt_Key)).setText(sharedText);
                    }
                }
            }
            return;
        }

        NdefMessage ndefMessage = nfcHelper.getNdefMessageFromIntent(intent);
        NdefRecord ndefRecord = nfcHelper.getFirstNdefRecord(ndefMessage);

        String text = nfcHelper.getTextFromNdefRecord(ndefRecord);

        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }

}
