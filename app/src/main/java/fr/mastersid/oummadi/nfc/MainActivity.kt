package fr.mastersid.oummadi.nfc

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import fr.mastersid.oummadi.nfc.databinding.ActivityMainBinding
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.experimental.and

class MainActivity : AppCompatActivity() {
    private var adapter: NfcAdapter? = null

    private var text1 :TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v("NFC","not working")
        onNewIntent(intent)
        var nfcAdapter : NfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is available.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC is not available on this device. This application may not work correctly.", Toast.LENGTH_LONG).show();
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                /*val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage

                }*/
                Log.v("NFC","it's work"+getData(rawMessages))
            }

        }
    }

    fun getUID(intent: Intent): String {
        val myTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        return myTag!!.id.toString()
    }

    fun test(intent : Intent){
    }
    fun getData(rawMsgs: Array<Parcelable>): String {
        val msgs = arrayOfNulls<NdefMessage>(rawMsgs.size)
        for (i in rawMsgs.indices) {
            msgs[i] = rawMsgs[i] as NdefMessage
        }

        val records = msgs[0]!!.records
        Log.v("NFC","it's work "+getTextFromNdefRecord(records[1]))
        var recordData = ""

        for (record in records) {
            recordData += record.toString() + "\n"
        }

        return recordData
    }

    fun getTextFromNdefRecord(ndefRecord : NdefRecord ) : String
    {
        var tagContent :String? = null;
        lateinit var  textEncoding : Charset
        try {
            var  payload = ndefRecord.getPayload();
            if((payload[0].and(128.toByte())).toInt() == 0){
                textEncoding = charset("UTF-8")
            }
            else{
                textEncoding = charset("UTF-16")
            }
            var languageSize = payload[0].and(63.toByte())
            tagContent = String(payload, languageSize + 1,payload.size - languageSize - 1, textEncoding);
        } catch ( e :UnsupportedEncodingException) {
            Log.e("getTextFromNdefRecord", e.message, e);
        }
        return tagContent.toString();
    }
}