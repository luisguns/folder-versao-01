package br.com.naveonline.folder;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class PiberApplication extends MultiDexApplication {

    private static PiberApplication outInstance = new PiberApplication();

    public static PiberApplication getInstance() {
        return outInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso build = builder.build();
        build.setIndicatorsEnabled(false);
        build.setLoggingEnabled(false);
        Picasso.setSingletonInstance(build);
    }
}
