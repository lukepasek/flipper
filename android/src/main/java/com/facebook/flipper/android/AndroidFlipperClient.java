/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.flipper.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.facebook.flipper.BuildConfig;
import com.facebook.flipper.core.FlipperClient;
import javax.annotation.Nullable;

public final class AndroidFlipperClient {
  private static boolean sIsInitialized = false;
  private static FlipperThread sFlipperThread;
  private static FlipperThread sConnectionThread;
  private static final String[] REQUIRED_PERMISSIONS = new String[] {
      "android.permission.INTERNET",
      "android.permission.ACCESS_WIFI_STATE"
  };
  private static final String SHARED_PREF_FILE_NAME = "flipper.config";

  public static synchronized FlipperClient getInstance(
      Context context, String id, String deviceName, String processName, String packageName) {
    if (!sIsInitialized) {
      if (!(BuildConfig.IS_INTERNAL_BUILD || BuildConfig.LOAD_FLIPPER_EXPLICIT)) {
        Log.e("Flipper", "Attempted to initialize in non-internal build");
        return null;
      }
      checkRequiredPermissions(context);
      sFlipperThread = new FlipperThread("FlipperEventBaseThread");
      sFlipperThread.start();
      sConnectionThread = new FlipperThread("FlipperConnectionThread");
      sConnectionThread.start();

      final Context app = context.getApplicationContext() == null ? context : context.getApplicationContext();

      // exempt this disk read/write as this is a debug tool
      StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
      String privateAppDirectory;
      try {
        privateAppDirectory = context.getFilesDir().getAbsolutePath();
      } finally {
        StrictMode.setThreadPolicy(oldPolicy);
      }
      FlipperClientImpl.init(
          sFlipperThread.getEventBase(),
          sConnectionThread.getEventBase(),
          // getInsecurePort(app, FlipperProps.getInsecurePort()),
          // getSecurePort(app, FlipperProps.getSecurePort()),
          // getAltInsecurePort(app, FlipperProps.getAltInsecurePort()),
          // getAltSecurePort(app, FlipperProps.getAltSecurePort()),
          getInsecurePort(app, FlipperProps.DEFAULT_INSECURE_PORT),
          getSecurePort(app, FlipperProps.DEFAULT_SECURE_PORT),
          getAltInsecurePort(app, FlipperProps.DEFAULT_ALT_INSECURE_PORT),
          getAltSecurePort(app, FlipperProps.DEFAULT_ALT_SECURE_PORT),
          getServerHost(app),
          "Android",
          deviceName,
          id,
          processName,
          packageName,
          privateAppDirectory);
      sIsInitialized = true;
    }
    return FlipperClientImpl.getInstance();
  }

  private static String getSharedPreferencesValue(Context context, String fileName, String key, String defaultValue) {
    SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(fileName,
        Context.MODE_PRIVATE);
    if (sharedPreferences != null) {
      String value = sharedPreferences.getString(key, defaultValue);
      if (value != null) {
        Log.i("Flipper",
            "Using server " + key + " from shared preferences ('" + SHARED_PREF_FILE_NAME + "'): '" + value + "'");
        return value;
      }
    } else {
      Log.i("Flipper",
          "Shared preferences ('" + SHARED_PREF_FILE_NAME + "') not available - using default value for '" + key + "': "
              + defaultValue);
    }
    return defaultValue;
  }

  private static int getInsecurePort(Context app, int defaultPort) {
    String port = getSharedPreferencesValue(app, SHARED_PREF_FILE_NAME, "insecure_port", null);
    if (port != null) {
      return Integer.parseInt(port);
    }
    return defaultPort;
  }

  private static int getSecurePort(Context app, int defaultPort) {
    String port = getSharedPreferencesValue(app, SHARED_PREF_FILE_NAME, "secure_port", null);
    if (port != null) {
      return Integer.parseInt(port);
    }
    return defaultPort;
  }

  private static int getAltInsecurePort(Context app, int defaultPort) {
    String port = getSharedPreferencesValue(app, SHARED_PREF_FILE_NAME, "alt_insecure_port", null);
    if (port != null) {
      return Integer.parseInt(port);
    }
    return defaultPort;
  }

  private static int getAltSecurePort(Context app, int defaultPort) {
    String port = getSharedPreferencesValue(app, SHARED_PREF_FILE_NAME, "alt_secure_port", null);
    if (port != null) {
      return Integer.parseInt(port);
    }
    return defaultPort;
  }

  public static synchronized FlipperClient getInstance(Context context) {
    final Context app = context.getApplicationContext() == null ? context : context.getApplicationContext();
    return getInstance(
        context, getId(), getFriendlyDeviceName(), getRunningAppName(app), getPackageName(app));
  }

  @Nullable
  public static synchronized FlipperClient getInstanceIfInitialized() {
    if (!sIsInitialized) {
      return null;
    }
    return FlipperClientImpl.getInstance();
  }

  static void checkRequiredPermissions(Context context) {
    // Don't terminate for compatibility reasons. Not all apps have
    // ACCESS_WIFI_STATE permission.
    for (String permission : REQUIRED_PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
        Log.e(
            "Flipper",
            String.format("App needs permission \"%s\" to work with Flipper.", permission));
      }
    }
  }

  static boolean isRunningOnGenymotion() {
    return Build.FINGERPRINT.contains("vbox");
  }

  static boolean isRunningOnStockEmulator() {
    return Build.FINGERPRINT.contains("generic") && !Build.FINGERPRINT.contains("vbox");
  }

  static String getId() {
    return Build.SERIAL;
  }

  static String getFriendlyDeviceName() {
    if (isRunningOnGenymotion()) {
      // Genymotion already has a friendly name by default
      return Build.MODEL;
    } else {
      return Build.MODEL + " - " + Build.VERSION.RELEASE + " - API " + Build.VERSION.SDK_INT;
    }
  }

  static String getServerHost(Context context) {
    String configuredHost = getSharedPreferencesValue(context, SHARED_PREF_FILE_NAME, "host", null);
    if (configuredHost != null) {
      return configuredHost;
    }
    if (isRunningOnStockEmulator() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      // adb reverse was added in lollipop, so before this
      // hard code host ip address.
      // This means it will only work on emulators, not physical devices.
      return "10.0.2.2";
    } else if (isRunningOnGenymotion()) {
      // This is hand-wavy but works on but ipv4 and ipv6 genymotion
      final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      final WifiInfo info = wifi.getConnectionInfo();
      final int ip = info.getIpAddress();
      return String.format("%d.%d.%d.2", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff));
    } else {
      // Running on physical device or modern stock emulator.
      // Flipper desktop will run `adb reverse` to forward the ports.
      return "localhost";
    }
  }

  static String getRunningAppName(Context context) {
    return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
  }

  static String getPackageName(Context context) {
    return context.getPackageName();
  }
}
