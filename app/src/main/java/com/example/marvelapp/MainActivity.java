package com.example.marvelapp;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView1;
    private MediaController mediaController;
    private final int CODE_ONE_VIDEO = 1;

    private TextView YourName;
    private TextView setAboutMe;

    private String url = "https://www.google.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        requestMultiplePermissions();
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

                String path = getFilePathFromURI(MainActivity.this,VideoUri);
                Log.d("ioooossss",path);
                uploadVideo(path);
                videoView1.setVideoURI(VideoUri);
                videoView1.start();
                videoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }
            else
            {
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadVideo(String path){

        String pdfname = String.valueOf(Calendar.getInstance().getTimeInMillis());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VideoInterface.VIDEOURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        //Create a file object using file path
        File file = new File(path);
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("filename", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), pdfname);

        VideoInterface getResponse = retrofit.create(VideoInterface.class);
        Call<String> call = getResponse.uploadImage(fileToUpload, filename);
        Log.d("beforeCAllBACK","asss");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("response found", response.body().toString());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    jsonObject.toString().replace("\\\\","");

                    if (jsonObject.getString("status").equals("true")) {

                        JSONArray dataArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            url = dataobj.optString("pathToFile");
                            Toast.makeText(MainActivity.this, ""+url, Toast.LENGTH_SHORT).show();
                            //tv.setText(url);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("on upload failure", call.toString());
            }
        });

    }

    private static final String IMAGE_DIRECTORY = "/demo_upload_gallery";


    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path

        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        File copyFile = new File(wallpaperDirectory + File.separator + Calendar.getInstance()
                .getTimeInMillis()+".mp4");
        // create folder if not exists

        copy(context, contentUri, copyFile);
        Log.d("vPath--->",copyFile.getAbsolutePath());

        return copyFile.getAbsolutePath();

    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int BUFFER_SIZE = 1024 * 2;

    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

/*
    private void getValue() {
        selectedPath = getPath();
        Toast.makeText(this, ""+selectedPath, Toast.LENGTH_SHORT).show();
    }

    public String getPath() {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(demo, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }
*/

}