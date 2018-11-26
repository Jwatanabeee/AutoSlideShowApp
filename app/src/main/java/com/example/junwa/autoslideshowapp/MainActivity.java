package com.example.junwa.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Cursor cursor;
    Button button1;
    Button button2;
    Button button3;
    Timer mTimer;
    Handler mHandler = new Handler();
    boolean auto_playing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button1.setText("再生/停止");
        button2 = (Button) findViewById(R.id.button2);
        button2.setText("進む");
        button3 = (Button) findViewById(R.id.button3);
        button3.setText("戻る");

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (auto_playing) {
                    if (mTimer == null) {
                        button1.setText("再生");
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }
                    } else {
                        auto_playing = true;
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                show();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        cursor.moveToNext();
                                    }
                                });
                            }
                        }, 2000, 2000);

                        button1.setText("停止");
                    }
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          // 「進むボタン」を押したら次の画像にカーソルが移動
                                          if (v.getId() == R.id.button2) {
                                              forward();

                                          }
                                      }
                                  });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(v.getId() == R.id.button3){
                        backward();

                    }
            }
        });






        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }



    //カーソルが最初の位置にあるときに画像を表示する
    private void getContentsInfo() {

        // 画像の情報を取得する



        ContentResolver resolver = getContentResolver();
        this.cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            show();
        }
    }


    //  現在のカーサー一を表示するshowメソッドを定義
    private void show() {

        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
    }

    //cursorを進めるメソッド
    private void forward() {

        if (cursor.moveToNext()) {
            show();
        }
        else {
            cursor.moveToFirst();
            show();

        }
    }

    //cursorを戻すメソッド
    private void backward(){

        if(cursor.moveToPrevious()) {
            show();
        }
        else{
            cursor.moveToLast();
            show();
        }

    }







    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

        }

