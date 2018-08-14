package com.huangjacky.teleows;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    protected void showtext(String txt){
        Toast.makeText(MainActivity.this, txt, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.SNI = false;
                HttpClient httpClient = Helper.getHttpClient();
                HttpGet get = new HttpGet("https://hec-waf.teleows.com");
                try {
                    HttpResponse httpResponse = httpClient.execute(get);
                    showtext(Integer.toString(httpResponse.getStatusLine().getStatusCode()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.SNI = true;
                HttpClient httpClient = Helper.getHttpClient();
                HttpGet get = new HttpGet("https://hec-waf.teleows.com");
                try {
                    HttpResponse httpResponse = httpClient.execute(get);
                    showtext(Integer.toString(httpResponse.getStatusLine().getStatusCode()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.SNI = false;
                HttpClient httpClient = Helper.getHttpClient();
                HttpGet get = new HttpGet("https://www.huaweicloud.com");
                try {
                    HttpResponse httpResponse = httpClient.execute(get);
                    showtext(Integer.toString(httpResponse.getStatusLine().getStatusCode()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
