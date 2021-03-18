package com.hog2020.ex77;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText etmsg;
    EditText ettitle;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etmsg= findViewById(R.id.et_msg);
        ettitle=findViewById(R.id.ed_title);
        tv=findViewById(R.id.tv);
    }

    public void clickBtn(View view) {
        //네트워크 작업은 항상 별도 Thread
        new Thread(){
            @Override
            public void run() {

                //서버로 보낼 데이터들
                String title= ettitle.getText().toString();
                String msg=etmsg.getText().toString();

                //get 방식으로 보낼 서버의 주소
                String serveUrl="http://hog2069.dothome.co.kr/Android/getTest.php";

                //URL 에는 한글 및 특수문자 사용불가- 한글을 URL 에 사용될 수 있도록 암호화(인코딩)
                try {
                    title = URLEncoder.encode(title,"utf-8");
                    msg = URLEncoder.encode(msg,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //get 방식은 URL 뒤에 물음표를 쓰고 요청파라미터 값을(title,msg)을 전송
                String geturl=serveUrl+"?title="+title+"&msg="+msg;

                //서버와 연결작업
                try {
                    URL url= new URL(geturl);

                    //이미 서버주소에 값들이 붙어서 전송되기 때문에 별도의 전송 작업이 필요없음
                    //즉 OutputStream 은 필요하지 없음

                    //서버(getTest.php)에서 echo 된 글씨를 읽어오기 위해 InputStream 이 필요
                    InputStream is = url.openStream();
                    InputStreamReader isr= new InputStreamReader(is);
                    BufferedReader reader =new BufferedReader(isr);

                    final StringBuffer buffer =new StringBuffer();

                    String line = reader.readLine();
                    while (line!=null){
                        buffer.append(line+"\n");
                        line= reader.readLine();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(buffer.toString());
                        }
                    });


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public void clickpost(View view) {
        new Thread(){
            @Override
            public void run() {
                String title=ettitle.getText().toString();
                String msg =etmsg.getText().toString();

                //Post 방식으로 데이터를 보낼 서버 주소
                String serveUrl="http://hog2069.dothome.co.kr/Android/postTest.php";

                try {
                    URL url = new URL(serveUrl);
                    //URL 은 InputStream 만 열 수 있음

                    //HTTP 통신 규약에 따라 데이터를 주고받는 역할을
                    //수행하는 URL 객체의 조수객체 가 있음
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST"); //반드시 대문자
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    //보낼 데이터
                    String data="title="+title+"&msg"+msg;

                    //데이터를 OutputStream 을 통해서 직접 내보내기
                    OutputStream os= connection.getOutputStream();
                    OutputStreamWriter writer =new OutputStreamWriter(os);

                    writer.write(data,0,data.length());
                    writer.flush();
                    writer.close();

                    //서버(postTest.php)에서 echo 시킬 문자열 읽어오기
                    InputStream is = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    final StringBuffer buffer = new StringBuffer();
                    String line = bufferedReader.readLine();
                    while(line!=null){
                        buffer.append(line+"\n");
                        line= bufferedReader.readLine();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(buffer.toString());
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}