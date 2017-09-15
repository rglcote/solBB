package com.rcsoft.solbb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rcsoft.solbb.model.EbookData;
import com.rcsoft.solbb.net.SOLNetworkDAO;

import java.io.IOException;

public class BookBuildActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private EditText mStoryIdView;
    private Uri selectedImageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_build);

        mStoryIdView = (EditText) findViewById(R.id.story_id_text);
    }

    public void buildEpub(View v) {

        // Check for a valid storyId, if the user entered one.
        String storyId = mStoryIdView.getText().toString();
        if (!TextUtils.isEmpty(storyId)) {
            //show error and stop
            mStoryIdView.setError(getString(R.string.error_invalid_password));
            mStoryIdView.requestFocus();
        } else {
            //start process
            SOLNetworkDAO.getInstance().setProgressView((TextView) findViewById(R.id.progress_text_view));
            EbookData data = SOLNetworkDAO.getInstance().buildEbookFromStoryId(mStoryIdView.getText().toString(), selectedImageURI);
        }
    }

    public void selectImage(View v) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {

                selectedImageURI = imageReturnedIntent.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 96, 96, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                ImageView view = (ImageView) findViewById(R.id.imageView);
                view.setImageBitmap(thumbnail);
                // Log.d(TAG, String.valueOf(bitmap));
            }
        } catch (IOException e) {
            Log.e("SOL", "Error loading thumbnail", e);
        }
    }

}