package io.github.fuadreza.firstjson;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Dibuat dengan kerjakerasbagaiquda oleh Shifu pada tanggal 25/06/2018.
 */

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String buatManggilService(String reqUrl) {
        String respon = null;
        try {
            URL url = new URL(reqUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //baca respon
            InputStream in = new BufferedInputStream(conn.getInputStream());
            respon = convertStreamToString(in);
        } catch (MalformedURLException er) {
            Log.e(TAG, "Malformed URL exception : " + er.getMessage());
        } catch (ProtocolException er) {
            Log.e(TAG, "Protocol exception : " + er.getMessage());
        } catch (IOException er) {
            Log.e(TAG, "IO exception : " + er.getMessage());
        } catch (Exception er) {
            Log.e(TAG, "Exception : " + er.getMessage());
        }
        return respon;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException er) {
            er.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException er) {
                er.printStackTrace();
            }
        }

        return sb.toString();
    }
}
