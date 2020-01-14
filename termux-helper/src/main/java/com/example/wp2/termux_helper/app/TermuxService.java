package com.example.wp2.termux_helper.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.wp2.termux_helper.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public final class TermuxService extends Service {

    private static final String NOTIFICATION_CHANNEL_ID = "termux_notification_channel";
    public static final String TERMUX_FAILSAFE_SESSION_ACTION = "com.termux.app.failsafe_session";

    /** Note that this is a symlink on the Android M preview. */
    @SuppressLint("SdCardPath")
    public static final String FILES_PATH = "/data/data/com.termux/files";
    public static final String PREFIX_PATH = FILES_PATH + "/usr";
    public static final String HOME_PATH = FILES_PATH + "/home";

    private static final String ACTION_STOP_SERVICE = "com.termux.service_stop";
    private static final String ACTION_LOCK_WAKE = "com.termux.service_wake_lock";
    private static final String ACTION_UNLOCK_WAKE = "com.termux.service_wake_unlock";
    /** Intent action to launch a new terminal session. Executed from TermuxWidgetProvider. */
    public static final String ACTION_EXECUTE = "com.termux.service_execute";

    public static final String EXTRA_ARGUMENTS = "com.termux.execute.arguments";

    public static final String EXTRA_CURRENT_WORKING_DIRECTORY = "com.termux.execute.cwd";
    private static final String EXTRA_EXECUTE_IN_BACKGROUND = "com.termux.execute.background";

    /** This service is only bound from inside the same process and never uses IPC. */
    class LocalBinder extends Binder {
        public final TermuxService service = TermuxService.this;
    }

    private final IBinder mBinder = new LocalBinder();

    private final Handler mHandler = new Handler();


    final List<BackgroundJob> mBackgroundTasks = new ArrayList<>();

    /** The wake lock and wifi lock are always acquired and released together. */
    private PowerManager.WakeLock mWakeLock;
    private WifiManager.WifiLock mWifiLock;

    /** If the user has executed the {@link #ACTION_STOP_SERVICE} intent. */
    boolean mWantsToStop = false;

    @SuppressLint("Wakelock")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_STOP_SERVICE.equals(action)) {
            mWantsToStop = true;
            stopSelf();
        } else if (ACTION_LOCK_WAKE.equals(action)) {
            if (mWakeLock == null) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, EmulatorDebug.LOG_TAG);
                mWakeLock.acquire();

                // http://tools.android.com/tech-docs/lint-in-studio-2-3#TOC-WifiManager-Leak
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, EmulatorDebug.LOG_TAG);
                mWifiLock.acquire();

                String packageName = getPackageName();
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    Intent whitelist = new Intent();
                    whitelist.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    whitelist.setData(Uri.parse("package:" + packageName));
                    whitelist.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        startActivity(whitelist);
                    } catch (ActivityNotFoundException e) {
                        Log.e(EmulatorDebug.LOG_TAG, "Failed to call ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", e);
                    }
                }

            }
        } else if (ACTION_UNLOCK_WAKE.equals(action)) {
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;

                mWifiLock.release();
                mWifiLock = null;
            }
        } else if (ACTION_EXECUTE.equals(action)) {
            Uri executableUri = intent.getData();
            String executablePath = (executableUri == null ? null : executableUri.getPath());

            String[] arguments = (executableUri == null ? null : intent.getStringArrayExtra(EXTRA_ARGUMENTS));
            String cwd = intent.getStringExtra(EXTRA_CURRENT_WORKING_DIRECTORY);

            if (intent.getBooleanExtra(EXTRA_EXECUTE_IN_BACKGROUND, false)) {
                BackgroundJob task = new BackgroundJob(cwd, executablePath, arguments, this, intent.getParcelableExtra("pendingIntent"));
                mBackgroundTasks.add(task);
            } else {
                boolean failsafe = intent.getBooleanExtra(TERMUX_FAILSAFE_SESSION_ACTION, false);

                // Transform executable path to session name, e.g. "/bin/do-something.sh" => "do something.sh".
                if (executablePath != null) {
                    int lastSlash = executablePath.lastIndexOf('/');
                    String name = (lastSlash == -1) ? executablePath : executablePath.substring(lastSlash + 1);
                    name = name.replace('-', ' ');
                }

                // Launch the main Termux app, which will now show the current session:
//                startActivity(new Intent(this, TermuxActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } else if (action != null) {
            Log.e(EmulatorDebug.LOG_TAG, "Unknown TermuxService action: '" + action + "'");
        }

        // If this service really do get killed, there is no point restarting it automatically - let the user do on next
        // start of {@link Term):
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {

    }


    @Override
    public void onDestroy() {
        File termuxTmpDir = new File(TermuxService.PREFIX_PATH + "/tmp");

        if (termuxTmpDir.exists()) {
            try {
                TermuxInstaller.deleteFolder(termuxTmpDir.getCanonicalFile());
            } catch (Exception e) {
                Log.e(EmulatorDebug.LOG_TAG, "Error while removing file at " + termuxTmpDir.getAbsolutePath(), e);
            }

            termuxTmpDir.mkdirs();
        }

        if (mWakeLock != null) mWakeLock.release();
        if (mWifiLock != null) mWifiLock.release();
    }


    public void onBackgroundJobExited(final BackgroundJob task) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBackgroundTasks.remove(task);
            }
        });
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        String channelName = "Termux";
        String channelDescription = "Notifications from Termux";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,importance);
        channel.setDescription(channelDescription);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }
}
