package com.hie2j.httpurlconnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button btn1;
    private Button btn2;
    private EditText edit_Information;
    private TextView text_Result;
    private ImageView img_Result;

    private static final int GET_IMG = 1001;
    private static final int GET_MESSAGE = 1002;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_Result = findViewById(R.id.txt_result);
        img_Result = findViewById(R.id.img_result);
        edit_Information = findViewById(R.id.edit_information);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String str = "上海";
                        useHttpURLConnectionGET(str);
                    }
                }).start();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String str = edit_Information.getText().toString();
                        useHttpURLConnectionPOST(str);
                    }
                }).start();
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == GET_IMG){
                    img_Result.setImageBitmap((Bitmap) msg.obj);
                    return true;
                }else if (msg.what == GET_MESSAGE){
                    text_Result.setText((CharSequence) msg.obj);
                    return true;
                }
                return false;
            }
        });
    }

    private void useHttpURLConnectionPOST(String str) {
        String weatherUrl = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
        try{
            URL url = new URL(weatherUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);


            String body = "theCityCode=".concat(str).concat("&theUserID=");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"utf-8"));
            bufferedWriter.write(body);
            bufferedWriter.flush();
            bufferedWriter.close();

            int code = httpURLConnection.getResponseCode();
            Log.e(TAG," code = " + code);

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = bufferedReader.readLine();
            while(line != null){
                stringBuffer.append(line);
                line = bufferedReader.readLine();
            }

            Log.e(TAG,"stringBuffer = " + stringBuffer.toString());
            Message message = Message.obtain();
            message.what = GET_MESSAGE;
            message.obj = stringBuffer.toString();
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void useHttpURLConnectionGET(String str) {
        String baseUrl = "http://www.baidu.com";
        String phoneUrl = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
        String imgUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1386436458,4179574259&fm=58&bpow=640&bpoh=960";
        try{
            URL url = new URL(imgUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            int code = httpURLConnection.getResponseCode();
            Log.e(TAG," code = " + code);

            InputStream inputStream = httpURLConnection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            Message msg = Message.obtain();
            msg.what = GET_IMG;
            msg.obj = bitmap;
            handler.sendMessage(msg);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = bufferedReader.readLine();
            while(line != null){
                stringBuffer.append(line);
                line = bufferedReader.readLine();
            }

            Log.e(TAG,"stringBuffer = " + stringBuffer.toString());
//            Message message = Message.obtain();
//            message.what = GET_MESSAGE;
//            message.obj = stringBuffer.toString();
//            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
