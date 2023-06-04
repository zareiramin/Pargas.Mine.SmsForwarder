package ir.pargasit.smsforwarder;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SmsListener extends BroadcastReceiver {

    SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(MyApplication.getContext());


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            boolean enableForward = prefs.getBoolean("enable_forward", false);
            if (enableForward == false) return;

            String ipString = prefs.getString("target_ip", "");
            String portString = prefs.getString("target_port", "");
            String sourceNumber = prefs.getString("source_number", "").replaceAll("\\s+", ""); //removes all whitespaces and non-visible characters;
            Boolean show_sms_form_all_numbers = prefs.getBoolean("show_sms_form_all_numbers", false);

            Integer port = Integer.parseInt(portString);


            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress().replaceAll("\\s+", "").replace("+","").trim(); //removes all whitespaces and non-visible characters

                        String msgBody = msgs[i].getMessageBody();
                        byte[] message = msgBody.getBytes();

                        boolean smsIsFromDeterminedSourceNumber = msg_from.startsWith(sourceNumber);

                        // send to network
                        if (smsIsFromDeterminedSourceNumber) {
                            // send to network
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        DatagramSocket s = new DatagramSocket();
                                        InetAddress ip = InetAddress.getByName(ipString);
                                        DatagramPacket p = new DatagramPacket(message, message.length, ip, port);
                                        s.send(p);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } else {
                            // send to ui
                            Intent intent2 = new Intent("from_my_sms_listener");
                            intent2.putExtra("msg", "عدم تطابق مبدا پیامک دریافتی" + ". مبدا پیامک=" + msg_from + " شماره تعیین شده به عنوان مبدا=" + sourceNumber);
                            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent2);
                        }

                        // send sms to ui
                        if (smsIsFromDeterminedSourceNumber || show_sms_form_all_numbers) {
                            // send to ui
                            Intent intent2 = new Intent("from_my_sms_listener");
                            intent2.putExtra("msg", "پیامک دریافتی از " + msg_from + ":" + msgBody);
                            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent2);
                        }
                    }

                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                    String s = "خطا در پردازش پیامک ورودی" + " " + e.getMessage();
                    Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}