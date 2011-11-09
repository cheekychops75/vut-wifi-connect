/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.kuratkoo.vutwificonnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class WifiStatusReceiver extends BroadcastReceiver {

    public static final String TAG = "VUTWiFiConnect|WifiStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        Log.d(TAG, String.valueOf(wifi.getConnectionInfo()));
        Log.d(TAG, String.valueOf(wifi.getWifiState()));
        Log.d(TAG, String.valueOf(wifi.getDhcpInfo()));

        try {
            if (networkInfo.isConnected()
                    && wifi.getConnectionInfo().getSSID().toLowerCase().startsWith("vutbrno")
                    && !Preferences.getString(context, "username", "").equals("")
                    && !Preferences.getString(context, "password", "").equals("")) {

                SystemClock.sleep(2000);
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://wifigw.cis.vutbr.cz/login.php");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("auth", "any"));
                Log.d(TAG, "VUTBrno: " + Preferences.getString(context, "username", "") + ":" + Preferences.getString(context, "password", ""));
                nameValuePairs.add(new BasicNameValuePair("user", Preferences.getString(context, "username", "")));
                nameValuePairs.add(new BasicNameValuePair("password", Preferences.getString(context, "password", "")));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);

                if (response.getStatusLine().getStatusCode() == 200) {
                    char[] c = new char[20480];
                    int n;

                    InputStreamReader is = new InputStreamReader((InputStream) response.getEntity().getContent(), "iso-8859-2");
                    StringBuilder out = new StringBuilder();

                    while ((n = is.read(c)) != -1) {
                        out.append(new String(c, 0, n));
                    }
                    if (out.toString().contains("Přihlášení do WiFi sítě VUT bylo úspěšné.")) {
                        Toast.makeText(context, context.getText(R.string.login_success) + " " + wifi.getConnectionInfo().getSSID(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getText(R.string.unable_to_connect) + " " + context.getText(R.string.check_login), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, context.getText(R.string.unable_to_connect), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, context.getText(R.string.unable_to_connect) + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
