package com.neurospeech.wsclient;

import android.util.Xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by akash.kava on 20-04-2017.
 */

public class WSHttpPost {

    private final Map<String, List<String>> headers;
    HttpURLConnection connection;



    public WSHttpPost(URL url) throws Exception {
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        headers = connection.getHeaderFields();
        connection.setDoInput(true);
        connection.setDoOutput(true);


    }

    public void setHeader(String header, String value) {
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        headers.put(header, values);
    }

    public void setEntity(String xml, String utf8) throws Exception {
        OutputStream outputStream = connection.getOutputStream();
        byte[] bytes = xml.getBytes(Charset.forName("UTF-8"));
        outputStream.write(bytes);
    }

    public void setConnectionTimeout(int connectionTimeout) {
        connection.setConnectTimeout(connectionTimeout);
    }

    public void setTimeout(int timeout) {
        connection.setReadTimeout(timeout);
    }

    public InputStream getResponseStream() throws Exception {
        return connection.getInputStream();
    }
}
