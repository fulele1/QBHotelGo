package com.xaqb.unlock.CameraTool;




import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class AutoFocusCallback implements Camera.AutoFocusCallback {

	  private static final String TAG = AutoFocusCallback.class.getSimpleName();

	  private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

	  private Handler FoHandler;
	  private int FiMessage;

	  void setHandler(Handler oandler, int iMessage) {
	    this.FoHandler = oandler;
	    this.FiMessage = iMessage;
	  }

	  public void onAutoFocus(boolean success, Camera camera) {
	    if (FoHandler != null) {
	      Message message = FoHandler.obtainMessage(FiMessage, success);
	      if(success) message.arg1=1;
	      else message.arg1=0;
	      FoHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
	      FoHandler = null;
	    } else {
	      Log.w("focus", "Got auto-focus callback, but no handler for it");
	    }
	  }

	}
