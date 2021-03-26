package com.jorge.pinedo.ocr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextRecognizer detector;
    TextView textView;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);

        detector = new TextRecognizer.Builder(this).build();

        this.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"clikc");
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100 && resultCode==RESULT_OK){
            try {
                Log.e(TAG,"resutl");
                Log.e(TAG,data.getData().toString());
                InputStream stream=getContentResolver().openInputStream(data.getData());
                Bitmap bitmap= BitmapFactory.decodeStream(stream);
                stream.close();

                if(detector.isOperational() && bitmap!=null){
                    Frame frame=new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlock = detector.detect(frame);
                    StringBuilder stringBuilder=new StringBuilder();
                    for(int i=0;i<textBlock.size();i++){
                        TextBlock tb=textBlock.get(i);
                        stringBuilder.append(tb.getValue());

                    }

                    if(textBlock.size() == 0){
                        textView.setText("Scan Failed");
                    }else{
                        textView.setText(stringBuilder.toString());
                    }
                }else{
                    textView.setText("Invalid Image");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                textView.setText("Problemas encore");
            } catch (IOException e) {
                e.printStackTrace();
                textView.setText("Problemas encore");
            }

        }
    }

    public void readImage(View view){



        Bitmap bitmap=BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.texto);

        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
            Toast.makeText(getApplicationContext(),"could not get text",Toast.LENGTH_LONG).show();
        }else{
            Frame frame=new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            StringBuilder sb=new StringBuilder();
            items.size();

            for(int i=0;i<items.size();i++){
                TextBlock myItem=items.get(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }

            textView.setText(sb.toString());


        }
    }

    @Override
    protected void onStop() {
        detector.release();
        super.onStop();
    }
}
