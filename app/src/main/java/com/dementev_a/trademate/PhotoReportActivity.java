package com.dementev_a.trademate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageSwitcher;
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
import java.util.Date;

public class PhotoReportActivity extends AppCompatActivity {
    private TextView header, errorTV;
    private ProgressBar progressBar;
    private ImageSwitcher imageSwitcher;
    private SharedPreferencesEngine spePictures;
    private LayoutInflater inflater;
    private String name, accessToken;
    private API api;
    private Uri currentPhotoUri;
    private File image;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_report);
        header = findViewById(R.id.photo_report_activity_header);
        progressBar = findViewById(R.id.photo_report_activity_progress_bar);
        errorTV = findViewById(R.id.photo_report_activity_error_tv);
        imageSwitcher = findViewById(R.id.photo_report_activity_image_switcher);
        name = getIntent().getStringExtra(IntentConstants.PHOTO_REPORT_NAME_INTENT_KEY);
        header.setText(name);
        SharedPreferencesEngine speUser = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        accessToken = speUser.getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY);
        spePictures = new SharedPreferencesEngine(this, name + getString(R.string.shared_preferences_pictures));
        api = new API(this, handler);

        inflater = LayoutInflater.from(this);
        int picturesQuality = spePictures.count();
        if (picturesQuality > 0) {
            for (int i = 1; i <= picturesQuality; i++) {
                ImageView imageView = (ImageView) inflater.inflate(R.layout.picture, imageSwitcher, false);
                imageView.setImageURI(Uri.parse(spePictures.getString("picture" + i)));
                imageSwitcher.addView(imageView);
            }
        } else {
            errorTV.setText(R.string.photo_report_activity_error_tv_photos_text);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            new ReactOnStatus(bundle, errorTV) {
                @Override
                public void success() {
                    switch (msg.what) {
                        case API.GET_PHOTOS_OF_REPORT_HANDLER: {
                            if (bundle.getInt(BundleEngine.TOTAL_PHOTOS_KEY_BUNDLE) == 0) {
                                errorTV.setText(R.string.photo_report_activity_error_tv_photos_text);
                            } else {
                                errorTV.setText("");
                                imageSwitcher.removeAllViews();
                                for (int i = 0; i < bundle.getInt(BundleEngine.TOTAL_PHOTOS_KEY_BUNDLE); i++) {
                                    byte[] byteArray = bundle.getByteArray("photo"+i);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                    ImageView imageView = (ImageView) inflater.inflate(R.layout.picture, imageSwitcher, false);
                                    imageView.setImageBitmap(bitmap);
                                    imageSwitcher.addView(imageView);
                                }
                            }
                        } break;
                        case API.PUT_PHOTO_HANDLER_NUMBER: {

                        } break;
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

            ImageView imageView = (ImageView) inflater.inflate(R.layout.picture, imageSwitcher, false);
            imageView.setImageURI(currentPhotoUri);

//            new Thread(() -> {
//                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                api.putPhoto(accessToken, baos.toByteArray(), name);
//                try {
//                    baos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();

            imageSwitcher.addView(imageView);
            spePictures.putString("picture" + (spePictures.count()+1), currentPhotoUri.toString());
        }
    }

    private void saveFullImage() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
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

    public void onSwitcherClick(View v) {
        imageSwitcher.showNext();
    }
}