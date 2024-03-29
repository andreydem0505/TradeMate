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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.intent.IntentConstants;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.widgets.ReactOnStatus;
import com.dementev_a.trademate.widgets.WidgetsEngine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoReportActivity extends AppCompatActivity {
    private TextView errorTV;
    private ProgressBar progressBar;
    private LinearLayout imagesLayout;
    private FloatingActionButton sendBtn;
    private LayoutInflater inflater;
    private ImageView toDelete;
    private String name, accessToken;
    private API api;
    private Uri currentPhotoUri;
    private File image;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_TradeMate);
        setContentView(R.layout.activity_photo_report);
        TextView header = findViewById(R.id.photo_report_activity_header);
        progressBar = findViewById(R.id.photo_report_activity_progress_bar);
        errorTV = findViewById(R.id.photo_report_activity_error_tv);
        sendBtn = findViewById(R.id.photo_report_activity_send_email_btn);
        imagesLayout = findViewById(R.id.photo_report_activity_images_layout);
        name = getIntent().getStringExtra(IntentConstants.PHOTO_REPORT_NAME_INTENT_KEY);
        header.setText(name);
        SharedPreferencesEngine speUser = new SharedPreferencesEngine(this, getString(R.string.shared_preferences_user));
        accessToken = speUser.getString(SharedPreferencesEngine.ACCESS_TOKEN_KEY);
        api = new API(this, handler);
        api.getPhotosOfReport(accessToken, name);
        inflater = LayoutInflater.from(this);
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
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            if (bundle.getInt(BundleEngine.TOTAL_PHOTOS_KEY_BUNDLE) == 0) {
                                errorTV.setText(R.string.photo_report_activity_error_tv_photos_text);
                            } else {
                                errorTV.setText("");
                                imagesLayout.removeAllViews();
                                for (int i = 0; i < bundle.getInt(BundleEngine.TOTAL_PHOTOS_KEY_BUNDLE); i++) {
                                    byte[] byteArray = bundle.getByteArray("photo"+i);
                                    long id = bundle.getLong("id"+i);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                    ImageView imageView = (ImageView) inflater.inflate(R.layout.picture, imagesLayout, false);
                                    imageView.setImageBitmap(bitmap);
                                    imageView.setOnLongClickListener(v -> {
                                        toDelete = imageView;
                                        WidgetsEngine.showDeletePhotoDialog(getSupportFragmentManager(), handler, id);
                                        return false;
                                    });
                                    imagesLayout.addView(imageView);
                                }
                            }
                        } break;
                        case API.PUT_PHOTO_HANDLER_NUMBER: {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            errorTV.setText("");
                        } break;
                        case API.SEND_PHOTO_REPORT_TO_EMAIL_HANDLER_NUMBER: {
                            sendBtn.setOnClickListener(PhotoReportActivity.this::onSendPhotosClickBtn);
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            errorTV.setTextColor(getColor(R.color.green));
                            errorTV.setText(R.string.photo_report_activity_email_was_send_warning);
                        } break;
                        case API.DELETE_DIALOG_HANDLER_NUMBER: {
                            progressBar.setVisibility(ProgressBar.VISIBLE);
                            api.deletePhotoReport(accessToken, name);
                        } break;
                        case API.DELETE_DIALOG_PHOTO_HANDLER_NUMBER: {
                            api.deletePhoto(accessToken, name, bundle.getLong(BundleEngine.ID_KEY_BUNDLE));
                            imagesLayout.removeView(toDelete);
                        } break;
                        case API.DELETE_PHOTO_REPORT_HANDLER_NUMBER: {
                            Intent intent = new Intent();
                            intent.putExtra(IntentConstants.TODO_INTENT_KEY, IntentConstants.TODO_DELETE);
                            intent.putExtra(IntentConstants.NAME_INTENT_KEY, name);
                            setResult(RESULT_OK, intent);
                            finish();
                        } break;
                    }
                }

                @Override
                public void failure() {
                    super.failure();
                    if (msg.what == API.SEND_PHOTO_REPORT_TO_EMAIL_HANDLER_NUMBER)
                        sendBtn.setOnClickListener(PhotoReportActivity.this::onSendPhotosClickBtn);
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

            ImageView imageView = (ImageView) inflater.inflate(R.layout.picture, imagesLayout, false);
            imageView.setImageURI(currentPhotoUri);
            imagesLayout.addView(imageView);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            errorTV.setTextColor(getColor(R.color.red));
            errorTV.setText(R.string.photo_report_activity_add_photo_error_text_warning);

            new Thread(() -> api.putPhoto(accessToken, getBytesFromImageView(imageView), name)).start();

            image.delete();
        }
    }

    private void saveFullImage() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "WEBP_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,
                ".webp",
                storageDir
        );
        currentPhotoUri = FileProvider.getUriForFile(this,
                "com.dementev_a.android.fileprovider",
                image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    public void onSendPhotosClickBtn(View v) {
        sendBtn.setOnClickListener(null);
        errorTV.setText(R.string.photo_report_activity_add_photo_error_text_warning);
        api.sendPhotoReport(accessToken, name);
    }

    public byte[] getBytesFromImageView(@NotNull ImageView imageView) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 0, baos);
        return baos.toByteArray();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_report, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.photo_report_menu_action_delete: {
                WidgetsEngine.showDeleteDialog(getSupportFragmentManager(), handler, name);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}