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
import android.widget.Toast;

import com.rcsoft.solbb.model.EbookData;
import com.rcsoft.solbb.net.SOLNetworkDAO;
import com.rcsoft.solbb.utils.PublishingAsyncTask;

import java.io.IOException;

public class BookBuildActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private EditText mStoryIdView;
    private Uri selectedImageURI = null;
    private RetrieveSOLContentTask mBookBuildTask = null;

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
            mBookBuildTask = new RetrieveSOLContentTask(storyId);
            mBookBuildTask.execute();
        } else {
            //show error and stop
            mStoryIdView.setError(getString(R.string.story_id_requested));
            mStoryIdView.requestFocus();
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

    public void finish(){
        Toast.makeText(this, "Book creation successful", Toast.LENGTH_SHORT).show();
//        //successful - go to next screen
//        Intent intent = new Intent(this, BookBuildActivity.class);
//        startActivity(intent);
    }

    private class RetrieveSOLContentTask extends PublishingAsyncTask<Void, String, Boolean> {

        private Exception exception;
        private String storyId;
        TextView mProgressView = (TextView) findViewById(R.id.progress_text_view);


        public RetrieveSOLContentTask (String storyId) {
            this.storyId = storyId;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            for (String val : values) {
                mProgressView.append(val);
            }
        }

        protected Boolean doInBackground(Void... voids) {
            try {
                //start process
                SOLNetworkDAO.getInstance().setCaller(this);
                EbookData data = SOLNetworkDAO.getInstance().buildEbookFromStoryId(storyId, selectedImageURI);
                return true;
            } catch (Exception e) {
                this.exception = e;
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            // TODO: check this.exception
            // TODO: do something with the feed

            mBookBuildTask = null;

            if (success) {
                finish();
            } else {
                mStoryIdView.setError(getString(R.string.error_bookcreation_failed));
                mStoryIdView.requestFocus();
            }

        }
    }


}