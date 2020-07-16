package com.example.marvelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView1;
    private MediaController mediaController;
    private final int CODE_ONE_VIDEO = 1;

    private TextView YourName;
    private TextView setAboutMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //    setMediaCont();

    }

    public void init(){
        YourName = findViewById(R.id.YourName);
        setAboutMe = findViewById(R.id.setAboutMe);
        this.videoView1 = findViewById(R.id.CoverVideo);
        this.mediaController = new MediaController(this);
    }

    public void setName(View view)
    {
        showPopUp(view, YourName);
    }

    public void setAboutMe(View view)
    {
        showPopUp(view, setAboutMe);
    }


    private void showPopUp(View view, final TextView textView)
    {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.edit_text_pop_up_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        final EditText editText = popupView.findViewById(R.id.editTextPopupWindow);
        Button b = popupView.findViewById(R.id.done);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str = editText.getText().toString();
                textView.setText(str);
                popupWindow.dismiss();
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void uploadCoverVideo(View view)
    {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),CODE_ONE_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CODE_ONE_VIDEO && resultCode == RESULT_OK){
            Uri VideoUri = data.getData();
            if(VideoUri!=null)
            {
                videoView1.setVideoURI(VideoUri);
                videoView1.start();
                videoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }

        }
    }

/*
    private  void init(){
        this.videoView1 = findViewById(R.id.videoView1);
        this.mediaController = new MediaController(this);
    }

    private void setMediaCont(){
        mediaController.setMediaPlayer(videoView1);
        mediaController.setAnchorView(videoView1);
        videoView1.setMediaController(mediaController);
    }*/
}