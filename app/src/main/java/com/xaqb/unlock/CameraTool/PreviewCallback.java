package com.xaqb.unlock.CameraTool;


import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;


final class PreviewCallback implements Camera.PreviewCallback {

	  private static final String TAG = PreviewCallback.class.getSimpleName();

	 
	  private Handler FoHandler;
	  private int FiMessage;

	  

	  public void setHandler(Handler oHandler, int iMessage) {
		    this.FoHandler = oHandler;
		    this.FiMessage = iMessage;
		  }
	 
	  public void onPreviewFrame(byte[] data, Camera camera) {
		  if (FoHandler != null) {
		    	 Message message = FoHandler.obtainMessage(FiMessage, 0,
		          0, data);
		         message.sendToTarget();
		         FoHandler=null;
		     }
	  }
	  

	}
