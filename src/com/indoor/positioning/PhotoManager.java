package com.indoor.positioning;

import android.graphics.Bitmap;

public class PhotoManager {

	private static PhotoManager instance;
	private Bitmap bmp;
	
	public static PhotoManager getInstance() {
		if (instance == null) {
			instance = new PhotoManager();
		}
		return instance;
	}

	private PhotoManager() {
	}
	
	/**********************************************************************
	 * GETTERS AND SETTERS
	 *********************************************************************/
	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
}
