package com.indoor.positioning;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

public class ProcessActivity extends Activity{
	
	private Bitmap bmp;
	private TouchImageView background;
	private static final int RESULT_LOAD_IMAGE = 1;
	private WifiManager mainWifi;
	private List<ScanResult> wifiList;
	private List<ScanResult> customWifiList;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processing);
		
		background = (TouchImageView) findViewById(R.id.map);
		
		if(PhotoManager.getInstance() != null)
			if(PhotoManager.getInstance().getBmp() != null)
				bmp = PhotoManager.getInstance().getBmp();
			else
				getPhotoFromGallery();
		
		bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
		if(bmp.getWidth() > bmp.getHeight()){
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp,bmp.getWidth(),bmp.getHeight(),true);
			bmp = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
		}
		background.setImageBitmap(bmp);
		
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mainWifi.setWifiEnabled(true);
		
	    // wifi scaned value broadcast receiver 
	   handler = new Handler();
	   handler.postDelayed(wifiScanRunnable, 3000);
	   
	   drawRoutersPositions(120, 120);
	   drawRoutersPositions(140, 140);
	   drawRoutersPositions(160, 160);
	   
	   
	   registerReceiver(new BroadcastReceiver()
       {
           @Override
           public void onReceive(Context c, Intent intent) 
           {
              wifiList = mainWifi.getScanResults(); 
              customWifiList = mainWifi.getScanResults();
              customWifiList.clear();
              Log.e("!!!!!!!!!!!!3", ""+wifiList);
              for(int i = 0; i < wifiList.size(); i++){
	            if(i < 3)
	          	customWifiList.add(wifiList.get(i));
	          else if(customWifiList != null)
	          	if(customWifiList.size() != 0){
	          		Collections.sort(customWifiList, new Comparator<ScanResult>(){
						  @SuppressLint("NewApi")
						public int compare(ScanResult emp1, ScanResult emp2) {
							Log.e("!!!!!!!!!!!!1", ""+(int) (emp1.timestamp - emp2.timestamp));
						    return (int) (emp1.timestamp - emp2.timestamp);
						    
						  }
						});
	                  Log.e("!!!!!!!!!!!!1", ""+customWifiList);
	          		for(int j = 0; j<customWifiList.size(); j++){
	          			if(wifiList.get(i).level > customWifiList.get(j).level){
	          				customWifiList.remove(2);
	          				customWifiList.add(wifiList.get(i));
	          			}
	          		}
	          	}
              }
              startTriangulation(customWifiList);
              Log.e("!!!!!!!!!!!!2", ""+customWifiList);
           }
       }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
   }


	private void getPhotoFromGallery(){
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
			case RESULT_LOAD_IMAGE:
			if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
	            cursor.moveToFirst();
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	            background.setImageBitmap(BitmapFactory.decodeFile(picturePath));
	        }
		}
	}
	
	Runnable wifiScanRunnable = new Runnable() {
		
		@Override
		public void run() {
			mainWifi.startScan();
			handler.postDelayed(this, 3000);
			Log.e("!!!!!!!!!!!", "Start new scan");
		}
	};
	
	private void drawRoutersPositions(int x, int y){
		   bmp.setPixel(x, y,R.color.red_friends);
		   bmp.setPixel(x, y+1,R.color.red_friends);
		   bmp.setPixel(x, y-1,R.color.red_friends);
		   bmp.setPixel(x+1, y,R.color.red_friends);
		   bmp.setPixel(x+1, y-1,R.color.red_friends);
		   bmp.setPixel(x+1, y+1,R.color.red_friends);
		   bmp.setPixel(x-1, y,R.color.red_friends);
		   bmp.setPixel(x-1, y+1,R.color.red_friends);
		   bmp.setPixel(x-1, y-1,R.color.red_friends);
	}
	
	
	private void startTriangulation(List<ScanResult> customWifiList2){
		float W, Z, x, y, y2;
		Point A = new Point(120, 120);
		Point B = new Point(140, 140);
		Point C = new Point(160, 160);
		double distanceToA, distanceToB, distanceToC;
		 DecimalFormat df = new DecimalFormat("#.##");
		 distanceToA = calculateDistance((double)customWifiList2.get(0).level, customWifiList2.get(0).frequency);
		 distanceToB = calculateDistance((double)customWifiList2.get(1).level, customWifiList2.get(1).frequency);
		 distanceToC = calculateDistance((double)customWifiList2.get(2).level, customWifiList2.get(2).frequency);
		 
		 W = (float) (distanceToA*distanceToA - distanceToB*distanceToB - A.x*A.x - A.y*A.y + B.x*B.x + B.y*B.y);
		 Z = (float) (distanceToB*distanceToB - distanceToC*distanceToC - B.x*B.x - B.y*B.y + C.x*C.x+ C.y*C.y);
		 x = (W*(C.y-B.y) - Z*(B.y-A.y)) / (2* ((B.x - A.x)*(C.y * B.y) - (C.x - B.x)*(B.y-A.y)));
		 y = (W - 2*x*(B.x -A.x)) / (2* (B.y -A.y));
		 y2 = (Z - 2*x*(C.x-B.x)) / (2*(C.y-B.y));
		 
		 y = (y + y2) / 2;
         Log.d("!!!!!????!!!", y +" "+ x + "m");
	}
	
	private void startTriangulation2(List<ScanResult> customWifiList2){
		float W, Z, x, y, y2;
		Point A = new Point(120, 120);
		Point B = new Point(140, 140);
		Point C = new Point(160, 160);
		double distanceToA, distanceToB, distanceToC;
		 DecimalFormat df = new DecimalFormat("#.##");
		 distanceToA = calculateDistance((double)customWifiList2.get(0).level, customWifiList2.get(0).frequency);
		 distanceToB = calculateDistance((double)customWifiList2.get(1).level, customWifiList2.get(1).frequency);
		 distanceToC = calculateDistance((double)customWifiList2.get(2).level, customWifiList2.get(2).frequency);
		 
		 W = (float) (distanceToA*distanceToA - distanceToB*distanceToB - A.x*A.x - A.y*A.y + B.x*B.x + B.y*B.y);
		 Z = (float) (distanceToB*distanceToB - distanceToC*distanceToC - B.x*B.x - B.y*B.y + C.x*C.x+ C.y*C.y);
		 x = (W*(C.y-B.y) - Z*(B.y-A.y)) / (2* ((B.x - A.x)*(C.y * B.y) - (C.x - B.x)*(B.y-A.y)));
		 y = (W - 2*x*(B.x -A.x)) / (2* (B.y -A.y));
		 y2 = (Z - 2*x*(C.x-B.x)) / (2*(C.y-B.y));
		 
		 y = (y + y2) / 2;
         Log.d("!!!!!????!!!", y +" "+ x + "m");
         
	}
	
//	(CGPoint)getCoordinateWithBeaconA:(CGPoint)a beaconB:(CGPoint)b beaconC:(CGPoint)c distanceA:(CGFloat)dA distanceB:(CGFloat)dB distanceC:(CGFloat)dC {
//	    x = (W*(c.y-b.y) - Z*(b.y-a.y)) / (2 * ((b.x-a.x)*(c.y-b.y) - (c.x-b.x)*(b.y-a.y)));
//	    y = (W - 2*x*(b.x-a.x)) / (2*(b.y-a.y));
//	    //y2 is a second measure of y to mitigate errors
//	    y2 = (Z - 2*x*(c.x-b.x)) / (2*(c.y-b.y));
//
//	    y = (y + y2) / 2;
//	    return CGPointMake(x, y);
	
	public double calculateDistance(double levelInDb, double freqInMHz)    {
		   double exp = (27.55 - (20 * Math.log10(freqInMHz)) + levelInDb) / 20.0;
		   return Math.pow(10.0, exp);
		}
}
