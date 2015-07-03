package org.apache.cordova.example;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eclipsesource.v8.V8;
import com.eclipsesource.tabris.android.TabrisActivity;

import com.eclipsesource.tabris.android.internal.toolkit.view.Swipe;
import com.eclipsesource.tabris.android.internal.toolkit.view.SwipeAdapter;
import com.eclipsesource.tabris.client.core.model.Properties;
import com.eclipsesource.tabris.client.core.operation.CallOperation;
import com.eclipsesource.tabris.client.core.operation.CreateOperation;
import com.eclipsesource.tabris.client.core.operation.SetOperation;

import static com.eclipsesource.tabris.client.core.ProtocolConstants.*;
import static com.eclipsesource.tabris.client.core.util.ValidationUtil.*;

import com.eclipsesource.tabris.android.internal.toolkit.operator.AbstractAndroidOperator;
import com.eclipsesource.tabris.client.core.OperatorRegistry;


/**
 * This class echoes a string called from JavaScript.
 */
public class Echo extends CordovaPlugin {


  public static class SwipeOperator2 extends AbstractAndroidOperator {

   public static final String TYPE = "tabris.Swipe2";

   public SwipeOperator2( TabrisActivity activity ) {
     super( activity );
   }

   @Override
   public String getType() {
     return TYPE;
   }

   @Override
   public void create( CreateOperation operation ) {
     validateCreateOperation( operation );
     String parentId = operation.getProperties().getString( PROP_PARENT );
     Swipe swipe = findObjectById( parentId, Swipe.class );
     SwipeAdapter swipeAdapter = new SwipeAdapter( getActivity(), swipe );
     swipe.setAdapter( swipeAdapter );
     getObjectRegistry().register( operation.getTarget(), swipeAdapter, operation.getType() );
     getActivity().getRemoteObject( swipeAdapter ).addListen( EVENT_SWIPE );
     setItemCount( swipeAdapter, operation.getProperties() );
   }

   @Override
   public Object call( CallOperation operation ) {
     validateCallOperation( operation );
     SwipeAdapter swipeAdapter = findObjectById( operation.getTarget(), SwipeAdapter.class );
     Properties properties = operation.getProperties();
     if( hasMethod( operation, PROP_ADD ) ) {
       swipeAdapter.addItemAt( properties.getInteger( PROP_INDEX ),
           properties.getString( PROP_CONTROL ) );
     } else if( hasMethod( operation, PROP_REMOVE ) ) {
       swipeAdapter.removeItemsAt( properties.getList( PROP_ITEMS, Integer.class ) );
     } else if( hasMethod( operation, PROP_LOCK_LEFT ) ) {
       swipeAdapter.lockLeft( properties.getInteger( PROP_INDEX ) );
     } else if( hasMethod( operation, PROP_LOCK_RIGHT ) ) {
       swipeAdapter.lockRight( properties.getInteger( PROP_INDEX ) );
     } else if( hasMethod( operation, PROP_UNLOCK_LEFT ) ) {
       swipeAdapter.lockLeft( SwipeAdapter.UNLOCKED );
     } else if( hasMethod( operation, PROP_UNLOCK_RIGHT ) ) {
       swipeAdapter.lockRight( SwipeAdapter.UNLOCKED );
     }
     return null;
   }

   private boolean hasMethod( CallOperation operation, String method ) {
     return operation.getMethod().equals( method );
   }

   @Override
   public void set( SetOperation operation ) {
     SwipeAdapter swipeAdapter = findObjectById( operation.getTarget(), SwipeAdapter.class );
     Properties properties = operation.getProperties();
     setItemCount( swipeAdapter, properties );
     setActiveItem( swipeAdapter, properties );
   }

   private void setItemCount( SwipeAdapter swipeAdapter, Properties properties ) {
     Integer itemCount = properties.getInteger( PROP_ITEM_COUNT );
     if( itemCount != null ) {
       swipeAdapter.setItemCount( itemCount );
       swipeAdapter.notifyDataSetChanged();
     }
   }

   private void setActiveItem( SwipeAdapter swipeAdapter, Properties properties ) {
     Integer active = properties.getInteger( PROP_ACTIVE );
     if( active != null ) {
       swipeAdapter.setActiveItem( active );
     }
   }
 }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("echo")) {
            TabrisActivity activity = (TabrisActivity) cordova.getActivity();
            SwipeOperator2 operator = new  SwipeOperator2(activity);
            OperatorRegistry operatorRegistry = activity.getWidgetToolkit().getOperatorRegistry();
            operatorRegistry.register( operator.getType(), operator );
            this.echo("done", callbackContext);
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
