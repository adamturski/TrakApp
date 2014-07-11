package pl.com.turski.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.appspot.trak.shipment.Shipment;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import pl.com.turski.app.settings.SettingKey;

/**
 * User: Adam
 */
public class App extends Application {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "TRAK_APP";

    private static Context context;

    @Override
    public void onCreate() {
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static Shipment getShipmentService() {
        SharedPreferences settings = App.getAppContext().getSharedPreferences("pl.com.turski.trak.app", Context.MODE_PRIVATE);
        String serverUrl = settings.getString(SettingKey.SERVER_URL.getKey(), SettingKey.SERVER_URL.getDefValue());
        Shipment.Builder builder = new Shipment.Builder(
                new ApacheHttpTransport(), new GsonFactory(), null).setRootUrl(serverUrl);
        return builder.build();
    }


}