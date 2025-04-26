package com.example.pdfautomationandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Bundle;
import android.telephony.SmsManager;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

import shared.PdfUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // مراقبة مجلد SMS
        FileObserver smsObserver = new FileObserver("/path/to/sms/folder") {
            @Override
            public void onEvent(int event, String file) {
                if (event == FileObserver.CREATE && file.endsWith(".pdf")) {
                    processSmsFile(new File("/path/to/sms/folder", file));
                }
            }
        };
        smsObserver.startWatching();

        // مراقبة مجلد WhatsApp
        FileObserver whatsappObserver = new FileObserver("/path/to/whatsapp/folder") {
            @Override
            public void onEvent(int event, String file) {
                if (event == FileObserver.CREATE && file.endsWith(".pdf")) {
                    processWhatsAppFile(new File("/path/to/whatsapp/folder", file));
                }
            }
        };
        whatsappObserver.startWatching();
    }

    private void processSmsFile(File file) {
        String text = PdfUtils.extractTextFromPdf(file.getPath());
        String phone = PdfUtils.extractPhoneNumber(text);
        sendSms(phone, text);
        file.delete();
    }

    private void processWhatsAppFile(File file) {
        String text = PdfUtils.extractTextFromPdf(file.getPath());
        String phone = PdfUtils.extractPhoneNumber(text);
        sendViaWhatsApp(phone, file.getPath());
        file.delete();
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void sendViaWhatsApp(String phone, String pdfPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(pdfPath)));
        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }
}