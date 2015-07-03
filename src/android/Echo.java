package org.apache.cordova.example;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eclipsesource.v8.V8;
import com.eclipsesource.tabris.android.TabrisActivity;

/**
 * This class echoes a string called from JavaScript.
 */
public class Echo extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("echo")) {
            String message = args.getString(0);
            V8 v8 = V8.createV8Runtime();
            boolean result = 0;
            try {
              //result = v8.executeIntScript("1+2");
              result = this.interface.getActivity() instanceof TabrisActivity;
              message = message + result;
            } finally {
              v8.release();
            }
            this.echo(message, callbackContext);
            return true;
        }
        return false;
    }

    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
