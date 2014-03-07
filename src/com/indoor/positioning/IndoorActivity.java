package com.indoor.positioning;

import java.io.InputStream;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class IndoorActivity extends Activity {
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private Button mBtnStart;
	private Button mBtnQrCode;
	private Button mBtnExit;
	private Button mBtnGallery;
	
	private Bitmap bmp;
	ImageView img;
	
	OnClickListener btnQRCodeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(IndoorActivity.this, ZBarScannerActivity.class);
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		}
	};
	
	OnClickListener btnStartListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			moveToNextScreen();
		}
	};
	
	OnClickListener btnGalleryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			getPhotoFromGallery();
		}
	};
	
	OnClickListener btnExitListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indoor);
		
		mBtnStart = (Button) findViewById(R.id.button_start);
		mBtnQrCode = (Button) findViewById(R.id.button_qr_code);
		mBtnExit = (Button) findViewById(R.id.button_exit);
		mBtnGallery = (Button) findViewById(R.id.button_gallery);
		
		mBtnQrCode.setOnClickListener(btnQRCodeListener);
		mBtnStart.setOnClickListener(btnStartListener);
		mBtnExit.setOnClickListener(btnExitListener);
		mBtnGallery.setOnClickListener(btnGalleryListener);
		
		img = (ImageView) findViewById(R.id.imageView1);
	}

	public double calculateDistance(double signalLevelInDb, double freqInMHz) {
	    double exp = (27.55 - (20 * Math.log10(freqInMHz)) - signalLevelInDb) / 20.0;
	    return Math.pow(10.0, exp);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
			case ZBAR_SCANNER_REQUEST:
				new DownloadImageTask(bmp).execute(data.getStringExtra(ZBarConstants.SCAN_RESULT));
			//	downloadMap(data.getStringExtra(ZBarConstants.SCAN_RESULT));
				break;
				
			case RESULT_LOAD_IMAGE:
			if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
	            cursor.moveToFirst();
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	            PhotoManager.getInstance().setBmp(BitmapFactory.decodeFile(picturePath));
	        }
		}
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		 Bitmap bmImage;

	    public DownloadImageTask(Bitmap bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	       PhotoManager.getInstance().setBmp(result);
	    }
	}

	private void getPhotoFromGallery(){
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	protected void moveToNextScreen() {
		Intent intent = new Intent(this, ProcessActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.animation_slide_in_left_to_rigth, R.animator.animation_slide_out_left_to_right);
	}
	
}
