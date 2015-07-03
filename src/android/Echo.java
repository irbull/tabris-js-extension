package org.apache.cordova.example;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eclipsesource.v8.V8;
import android.view.View;
import android.widget.Switch;

import com.eclipsesource.tabris.android.TabrisActivity;
import com.eclipsesource.tabris.android.internal.toolkit.property.IPropertyHandler;
import com.eclipsesource.tabris.android.internal.toolkit.property.SwitchPropertyHandler;
import com.eclipsesource.tabris.client.core.RemoteObject;
import com.eclipsesource.tabris.client.core.model.Properties;
import com.eclipsesource.tabris.client.core.operation.CreateOperation;
import com.eclipsesource.tabris.client.core.operation.ListenOperation;

import static com.eclipsesource.tabris.client.core.ProtocolConstants.EVENT_CHECKED;
import static com.eclipsesource.tabris.client.core.util.ValidationUtil.*;

import com.eclipsesource.tabris.android.internal.toolkit.operator.AbstractAndroidOperator;
import com.eclipsesource.tabris.client.core.OperatorRegistry;


/**
 * This class echoes a string called from JavaScript.
 */
public class Echo extends CordovaPlugin {

  public static class SwitchOperator2 extends ButtonOperator {

    public static final String TYPE = "tabris.Switch2";

    private final SwitchPropertyHandler handler;

    public SwitchOperator( TabrisActivity activity ) {
      super( activity );
      handler = new SwitchPropertyHandler( activity );
    }

    @Override
    public String getType() {
      return TYPE;
    }

    @Override
    protected IPropertyHandler getPropertyHandler( Object object ) {
      return handler;
    }

    @Override
    public void create( CreateOperation operation ) {
      validateCreateOperation( operation );
      initiateNewView( operation, new Switch( getActivity() ) );
    }

    @Override
    public void listen( ListenOperation operation ) {
      super.listen( operation );
      validateListenOperation( operation );
      Properties properties = operation.getProperties();
      if( properties.hasProperty( EVENT_CHECKED ) ) {
        View view = findViewByTarget( operation );
        if( properties.getBoolean( EVENT_CHECKED ) ) {
          view.setOnClickListener( new SwitchClickListener( getActivity() ) );
        } else {
          view.setOnClickListener( null );
        }
      }
    }

    static class SwitchClickListener implements View.OnClickListener {

      private final TabrisActivity activity;

      public SwitchClickListener( TabrisActivity activity ) {
        this.activity = activity;
      }

      @Override
      public void onClick( View view ) {
        RemoteObject remoteObject = activity.getRemoteObject( view );
        remoteObject.notify( EVENT_CHECKED, EVENT_CHECKED, ( ( Switch )view ).isChecked() );
      }

    }

  }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("echo")) {
            TabrisActivity activity = (TabrisActivity) cordova.getActivity();
            SwitchOperator2 operator = new  SwitchOperator2(activity);
            OperatorRegistry operatorRegistry = activity.getWidgetToolkit().getOperatorRegistry();
            operatorRegistry.register( operator.getType(), operator );
            this.echo(operator.getType(), callbackContext);
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
