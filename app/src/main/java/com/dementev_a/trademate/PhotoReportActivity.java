package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.widgets.ReactOnStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class PhotoReportActivity extends AppCompatActivity {
    private ImageView photoIV;
    private TextView header, errorTV;
    private ProgressBar progressBar;
    private String name, accessToken;
    private API api;
    private Uri currentPhotoUri;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_report);
        header = findViewById(R.id.photo_report_activity_header);
        progressBar = findViewById(R.id.photo_report_activity_progress_bar);
        errorTV = findViewById(R.id.photo_report_activity_error_tv);
        photoIV = findViewById(R.id.photo);
        name = getIntent().getStringExtra(IntentConstants.PHOTO_REPORT_NAME_INTENT_KEY);
        header.setText(name);
        SharedPreferencesEngine spe = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        accessToken = spe.getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY);
        api = new API(this, handler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        api.getPhotosOfReport(accessToken, name);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Bundle bundle = msg.getData();
            new ReactOnStatus(bundle, errorTV) {
                @Override
                public void success() {
                    if (bundle.getInt(BundleEngine.TOTAL_PHOTOS_KEY_BUNDLE) == 0) {
                        errorTV.setText(R.string.photo_report_activity_error_tv_photos_text);
                    }
                }
            }.execute();
        }
    };

    public void onAddPhotoClickBtn(View v) throws IOException {
        saveFullImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            bitmap.recycle();
//            System.out.println(Arrays.toString(byteArray));
//            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            photoIV.setImageURI(currentPhotoUri);
        }
    }

    private void saveFullImage() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoUri = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }
}