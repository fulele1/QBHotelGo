package com.xaqb.unlock.CameraTool;


import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class TakePictureCallback implements Camera.PictureCallback {

  private static final String TAG = TakePictureCallback.class.getSimpleName();



  private Handler FoHandler;
  private int FiMessage;

  
  public void setHandler(Handler oHandler, int iMessage) {
	    this.FoHandler = oHandler;
	    this.FiMessage = iMessage;
	  }
 /* 
  public void onPreviewFrame(byte[] data, Camera camera) {
	  if (FoHandler != null) {
	    	 Message message = FoHandler.obtainMessage(FiMessage, 0,
	          0, data);
	         message.sendToTarget();
	         FoHandler=null;
	         Log.w("preview", "ok");
	         }
  }
  */

  public void onPictureTaken(byte[] data, Camera camera) {
     if (FoHandler != null) {
    	 Message message = FoHandler.obtainMessage(FiMessage, 0,
          0, data);
         message.sendToTarget();
         } else {
      Log.d(TAG, "Got preview callback, but no handler for it");
    }
  }


}
