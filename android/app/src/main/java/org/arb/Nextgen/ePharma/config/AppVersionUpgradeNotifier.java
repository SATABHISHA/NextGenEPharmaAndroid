package org.arb.Nextgen.ePharma.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.arb.Nextgen.ePharma.BuildConfig;

public enum AppVersionUpgradeNotifier {
    //---this enum is made for app version updateDCR notification

    INSTANCE;

    private static final String TAG = "AppVersionUpdateManager";

    private static final String PREFERENCES_APP_VERSION = "pref_app_version_upgrade";
    private static final String KEY_LAST_VERSION = "last_version";

    private SharedPreferences sharedPreferences;
    private VersionUpdateListener versionUpdateListener;
    private boolean isInitialized;

    public static synchronized void init(Context context, VersionUpdateListener versionUpdateListener) {
        if (context == null || versionUpdateListener == null) {
            throw new IllegalArgumentException(TAG + " : Context or VersionUpdateListener is null");
        }

        if (!INSTANCE.isInitialized) {
            INSTANCE.initInternal(context, versionUpdateListener);
        } else {
            Log.w(TAG, "Init called twice, ignoring...");
        }
    }

    private void initInternal(Context context, VersionUpdateListener versionUpdateListener) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_APP_VERSION, Context.MODE_PRIVATE);
        this.versionUpdateListener = versionUpdateListener;
        this.isInitialized = true;
        checkVersionUpdate();
    }

    private void checkVersionUpdate() {
        int lastVersion = getLastVersion();
        int currentVersion = getCurrentVersion();

        if (lastVersion < currentVersion) {
            if (versionUpdateListener.onVersionUpdate(currentVersion, lastVersion)) {
                upgradeLastVersionToCurrent();
            }
        }
    }

    private int getLastVersion() {
        return sharedPreferences.getInt(KEY_LAST_VERSION, 0);
    }

    private int getCurrentVersion() {
        return BuildConfig.VERSION_CODE;
    }

    public void upgradeLastVersionToCurrent() {
        sharedPreferences.edit().putInt(KEY_LAST_VERSION, getCurrentVersion()).apply();
    }

    public interface VersionUpdateListener {
        boolean onVersionUpdate(int newVersion, int oldVersion);
    }
}
