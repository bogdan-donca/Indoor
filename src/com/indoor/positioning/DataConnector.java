package com.indoor.positioning;

import android.content.Intent;

public class DataConnector {
	private Intent intentData;
	private int resultCode;
	private int requestCode;
	private String temporaryImagePath = null;
	
	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public String getTemporaryImagePath() {
		return temporaryImagePath;
	}

	public void setTemporaryImagePath(String temporaryImagePath) {
		this.temporaryImagePath = temporaryImagePath;
	}

	public void setIntentData(Intent data){
		this.intentData = data;

	}
	
	public Intent getIntentData(){
		return intentData;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	

	
}
