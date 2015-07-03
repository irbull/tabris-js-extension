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
import com.eclipsesource.tabris.client.core.RemoteObject;
import com.eclipsesource.tabris.client.core.model.Properties;
import com.eclipsesource.tabris.client.core.operation.CreateOperation;
import com.eclipsesource.tabris.client.core.operation.ListenOperation;

import static com.eclipsesource.tabris.client.core.util.ValidationUtil.*;

import com.eclipsesource.tabris.android.internal.toolkit.operator.AbstractAndroidOperator;
import com.eclipsesource.tabris.client.core.OperatorRegistry;

import com.eclipsesource.tabris.android.internal.toolkit.operator.*;
import com.eclipsesource.tabris.android.internal.toolkit.property.*;
import static com.eclipsesource.tabris.client.core.ProtocolConstants.*;
import static com.eclipsesource.tabris.client.core.util.ParamCheck.notNull;


/**
 * This class echoes a string called from JavaScript.
 */
public class Echo extends CordovaPlugin {

  public static class SwitchPropertyHandler<T extends Switch> extends ButtonPropertyHandler<T> {

    public SwitchPropertyHandler( TabrisActivity activity ) {
      super( activity );
    }

    @Override
    public void set( T switchButton, Properties properties ) {
      super.set( switchButton, properties );
      notNull( switchButton, Switch.class );
      notNull( properties, Properties.class );
      for( String key : properties.getAll().keySet() ) {
        switch( key ) {
          case PROP_CHECKED:
            setChecked( switchButton, properties );
          case PROP_TEXT_CHECKED:
            setTextChecked( switchButton, properties );
            break;
          case PROP_TEXT_UNCHECKED:
            setTextUnchecked( switchButton, properties );
            break;
        }
      }
    }

    private void setTextChecked( T switchButton, Properties properties ) {
      switchButton.setTextOn( properties.getString( PROP_TEXT_CHECKED ) );
    }

    private void setTextUnchecked( T switchButton, Properties properties ) {
      switchButton.setTextOff( properties.getString( PROP_TEXT_UNCHECKED ) );
    }

    private void setChecked( T switchButton, Properties properties ) {
      switchButton.setChecked( properties.getBoolean( PROP_CHECKED ) );
    }

    @Override
    public Object get( T switchButton, String property ) {
      notNull( switchButton, Switch.class );
      notNull( property, "property" );
      switch( property ) {
        case PROP_CHECKED:
          return getChecked( switchButton );
        case PROP_TEXT_CHECKED:
          return getTextChecked( switchButton );
        case PROP_TEXT_UNCHECKED:
          return getTextUnchecked( switchButton );
      }
      return super.get( switchButton, property );
    }

    private Object getTextChecked( T switchButton ) {
      return switchButton.getTextOn();
    }

    private Object getTextUnchecked( T switchButton ) {
      return switchButton.getTextOff();
    }

    private Object getChecked( T switchButton ) {
      return switchButton.isChecked();
    }

  }

  public static class SwitchOperator2 extends ButtonOperator {

    public static final String TYPE = "tabris.Switch2";

    private final SwitchPropertyHandler handler;

    public SwitchOperator2( TabrisActivity activity ) {
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
      if( properties.hasProperty( "checked") ) {
        View view = findViewByTarget( operation );
        if( properties.getBoolean( "checked") ) {
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
        remoteObject.notify( "checked", "checked", ( ( Switch )view ).isChecked() );
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
