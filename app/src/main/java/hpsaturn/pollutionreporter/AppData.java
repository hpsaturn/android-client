package hpsaturn.pollutionreporter;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.iamhabib.easy_preference.EasyPreference;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.internal.RxBleLog;

import dagger.hilt.android.HiltAndroidApp;
import hpsaturn.pollutionreporter.api.AqicnApiManager;

/**
 * Created by Antonio Vanegas @hpsaturn on 6/13/18.
 */

@HiltAndroidApp
public class AppData extends MultiDexApplication{

    private RxBleClient rxBleClient;
    private EasyPreference.Builder prefBuilder;

    public static RxBleClient getRxBleClient(Context context) {
        AppData application = (AppData) context.getApplicationContext();
        return application.rxBleClient;
    }

    public static EasyPreference.Builder getPrefBuilder(Context context){
        AppData application = (AppData) context.getApplicationContext();
        return application.prefBuilder;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rxBleClient = RxBleClient.create(this);
        RxBleClient.setLogLevel(RxBleLog.VERBOSE);
        prefBuilder = EasyPreference.with(this,"KEYS_APP_PREFERENCES");
        AqicnApiManager.getInstance().init(this);
    }
}
