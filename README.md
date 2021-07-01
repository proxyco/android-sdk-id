# Proxy SDK Examples for Android  
This repository contains an application that illustrates the basic usage of the Proxy ID SDK.
  
## Requirements:
-   Android SDK Platform 30 rev 3 (min is 23)
-   Android SDK Build-tools 30.0.2
-   Android SDK Command-line Tools 2.1
-   Android SDK Platform-tools 30.0.4
-   Gradle 6.3
-   Gradle Plugin for Android 4.0.1

## Setup

### Step  1:  Add dependencies
Add these repositories to your top-level (project) `build.gradle` file (contact Proxy for Jitpack authToken):
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io'
		        credentials { username authToken }
        }
    }
}
```

Add this to the `dependencies{}` section of your module-level (app) `build.gradle` file:
```
implementation ('com.github.proxyco:android:sdk:{release version}')
```

Add to the `android{}` section:
```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

Add to your pro-guard rules:
```
-keep class co.proxy.sdk.** { *; }
-keep interface co.proxy.sdk.** { *; }
-keepclassmembers,includedescriptorclasses class co.proxy.sdk.** { *; }
-keepclassmembers,includedescriptorclasses interface co.proxy.sdk.** {*; }
```

### Step  2:  Declare hardware requirements and permissions

The Proxy Mobile SDK uses Bluetooth Low Energy (BLE) to communicate with Proxy devices, which requires certain hardware and permissions. Here we describe how we use each of the requested permissions.

`ACCESS_FINE_LOCATION`  
We require this to detect nearby BLE devices (this is an Android requirement). Additionally, we use your location to set up geo-fences around places where you use Proxy, to automatically wake up the app at just the right time to interact with Proxy devices. We never track, store, or send your location anywhere.  
  

`ACCESS_BACKGROUND_LOCATION`  
This permission is part of the Location permission and is requested for Android 10+ devices in order to work with geo-fences while the app is in the background. Proxy will continue to work if this permission is not granted, but you may need to open the app in order to interact with Proxy devices.  
  

`ACCESS_NETWORK_STATE`  
We require this to detect when network connectivity is available, so we can perform network requests to the Proxy cloud service.  
  

`BLUETOOTH`  
We require this to control the Bluetooth radio on the phone.  
  

`INTERNET`  
We require this to communicate with the Proxy cloud service.  
  

`FOREGROUND_SERVICE`  
We require this to run the Proxy service process to make sure Proxy is always available and ready for use, without your users needing to launch the app.  
  

`RECEIVE_BOOT_COMPLETED`  
We require this to restart the Proxy service after the phone is rebooted.  
  

Most of the permissions are declared in SDK manifest file so it will be merged to your final manifest, Add these hardware requirements and permissions to your  `AndroidManifest.xml`  file:
```
<uses-feature android:name="android.hardware.bluetooth" android:required="true"/>
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
<uses-feature android:name="android.hardware.location.gps" android:required="false"/>
<uses-feature android:name="android.hardware.location.network" android:required="false"/>
```
Since using Bluetooth on Android devices requires location permission as well, for Proxy to work correctly, the user must grant "Location Permission" to your app, as well as turn on Bluetooth and Location Services on their phone.
```
On Android 10+ devices, there is now a tristate permission for location -  `Deny`,  `Allow all the time`, and  `Allow only while the app is in use`. When users upgrade from Android 9, the permission will be transferred as  `Allow only while the app is in use`.

On Android 11+ devices, the system enforces incremental location requests. If your app targets Android 11, you must request foreground location first and then, in a separate request, background location.
```

## Using the SDK

### Logging
If you are already using `Timber` for logging in your app, you can leverage our existing debug logs by planting a `DebugTree`:
```
Timber.plant(new Timber.DebugTree());
```
### Initializing
To initialize the SDK, you must first build the persistent notification used by the services. Displaying this visible notification is an Android requirement for running any persistent service on the phone.

The best place to do this is in your `Application.onCreate()` override:

```
NotificationInfo notificationInfo = new NotificationInfo.Builder()
  .setSmallIcon(R.mipmap.ic_launcher)
  .setTitle("Services Running")
  .setText("Tap to open app")
  .setLauncherActivity(co.proxy.sdk.example.MainActivity.class)
  .setColor(R.color.colorPrimary)
  .setGeoFenceNotificationId(GEO_FENCE_NOTIFICATION_ID)
  .setForegroundServiceNotificationId(FOREGROUND_SERVICE_NOTIFICATION_ID)
  .setNotificationChannelName(getString(R.string.app_name))
  .build();
```

Then, create the configuration to pass to the SDK:
```
ProxySDKConfig config = new ProxySDKConfig.Builder()
  .setAppId(getString(R.string.app_id))
  .setClientId(getString(R.string.client_id))
  .setDefaultNotificationInfo(notificationInfo)
  .setNearByEnabled(false)
  .build();
```
Client ID is a unique identifier for your application, [get yours here](https://docs.proxy.com/your-apps).

From this point, you should be able to compile your app and initialize the Proxy SDK:
```
ProxySDK.init(getApplication(), config);
```

### Authenticating the user

```
Check if the user is already authenticated with `ProxySDK.isAuth()`. This lets you to skip the authentication UI flow and display different UI in your app.
```

Proxy SDK includes standard user interface elements to make it simple to authenticate a Proxy user and request necessary privacy permissions and data consent within your app. It can take as few as four lines of code to get a user authenticated and emitting their Proxy signal from your app.

##### Configuring authentication UI

At a minimum, you must provide the email address of the user you wish to authenticate with the Proxy service. This email address is used to verify their ownership of their Proxy account. You can customize additional aspects of the authentication UI using an `Intent` (used to start the Authenticating Activity later on) with the following fields:

```
Intent intent = new Intent(this, AuthActivity.class);
intent.putExtra(AuthActivity.EMAIL_EXTRA, "email@example.com");
intent.putExtra(AuthActivity.APP_NAME_EXTRA, "Example App");
intent.putExtra(AuthActivity.APP_ICON_EXTRA, R.mipmap.ic_launcher);
```

##### Presenting authentication UI

Add the Authenticating Activity (with its theme) to the `<application>` section of your `AndroidManifest.xml`:

```
<activity
    android:name="co.proxy.sdk.ui.AuthActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar.TranslucentBackground">
</activity>
```
The activity `co.proxy.sdk.ui.AuthActivity` creates the navigation controller that contains the authentication UI. Simply start it with the `Intent` previously created, with the `REQUEST_CODE` of your choice:

```
startActivityForResult(intent, REQUEST_CODE);
```

Results of user authentication are reported via the callback `onActivityResult`. Check for `RESULT_OK` for the result code, which will be reported if the user authenticates successfully.

##### Branding authentication UI

By default, the user interface elements to authenticate a user display the Proxy logo and color. To remove the logo and use your own branded color, simply create a SDK UI Theme with the `BRANDED` mode and your primary color:

```
ProxySDKUITheme uiTheme = new ProxySDKUITheme.Builder()
  .setProxySDKUIThemeMode(ProxySDKUITheme.ProxySDKUIThemeMode.BRANDED)
  .setColor(R.color.colorPrimary)
  .build();
```

Then, add it to the SDK configuration object before building it:

```
config.setUITheme(uiTheme)
```

### Authenticating the user with custom UI

To verify the user, Proxy sends an email with a verification code to the user's email address. The user then enters the received verification code, which is passed back to the SDK to complete verification. This code is generally short-lived and must be used soon after it is sent.

First, let's send the user an email with a unique verification code. To do so, just call this function and pass in the user's email address:

```
ProxySDK.requestVerification(email, new RequestVerificationCallback());
```

If successful, an email will be sent to that user with a verification code. Typically, this will be a short numeric code, so that it can be easily entered in the app.

Once the user has entered their verification code, they can be authenticated to the Proxy Mobile SDK using the following call. The SDK then takes care of storing and refreshing their access tokens for communicating with the Proxy cloud service.

```
ProxySDK.requestTokenForCode(email, code, new RequestTokenForCodeCallback());
```

### Starting the Proxy signal

Now we have authenticated the user, we can start the Proxy service. This enables the user to start using their Proxy signal to access any Proxy device.

Start the services, passing in listeners to be notified of their state:

```
ProxySDK.startBleServices(true, new BleAdvertiserServiceListener(), new BleScannerServiceListener());
```

By default, the Proxy signal is set to restart automatically in many situations if it was running previously, such as after a phone reboot, app upgrade, or toggling of the Bluetooth radio or airplane mode. We recommend this approach for best reliability and user experience.

If you want to completely turn off the Proxy signal in your app and prevent automatic restart of the services, such as when a user has opted out of using Proxy features in your app, you may do so by toggling the "Signal Enabled Setting":

```
ProxySDK.getSignalEnabledSetting().set(false);
```

Both `start` methods take a Boolean first argument indicating how the services should treat this setting. If passed `true`, the services will start regardless of the current value of the "Signal Enabled Setting", and will reset its value to `true`. If passed `false`, the services will start only if "Signal Enabled Setting" is `true`.

### Stopping the Proxy signal

To stop the services and unregister the listeners at the same time:

```
ProxySDK.stopBleServices();
```

To unregister the listeners without stopping the services, such as when your main `Activity` is destroyed:

```
ProxySDK.unbindBleServices();
```

### Listening and reacting to Proxy service state changes

ou can pass a listener instance to each `start` call to listen for state changes and detect errors with the service. Listeners must implement these interfaces:

```
public interface BleAdvertiserServiceListener {
  /**
   * Advertiser status callback.
   * @param int status
   * @param int errorCode
   */
  void onAdvertiserStatus(int status, int errorCode);
}
```

```
public interface BleScannerServiceListener {
  /**
   * Scanner status callback.
   * @param int status
   * @param int errorCode
   */
  void onScannerStatus(int status, int errorCode);

  /**
   * Presence notify callback, called every time a device has been detected/updated.
   * @param Presence Fresh presence
   */
  void onPresence(Presence presence);

  /**
   * Called with a snapshot of the devices around you (when the service is started).
   * @param list Fresh presence list
   */
  void onPresenceList(List<Presence> list);
}
```

Status codes reported to listeners:

-   `0`: Service is disabled
-   `1`: Service is enabled

Error codes reported to listeners:

-   `16`: Bluetooth permission is denied.
-   `17`: Bluetooth adapter is disabled.
-   `18`: Device does not support Bluetooth.
-   `19`: Device does not support Bluetooth Low Energy central mode.
-   `20`: Device does not support Bluetooth Low Energy peripheral mode.
-   `21`: Failed to acquire a GATT Server connection.
-   `22`: Generic error, something unexpected went wrong.
-   `23`: Location permission is denied.
-   `24`: Location Services permission is denied.
-   `152`: User does not have the permission to access the scanned device.

You can also register a listener for the BLE client through the Proxy SDK. To do so, instantiate a `RxBleClientStateListener` and call:

```
ProxySDK.observeRxBleClientState(rxBleClientStateListener);
```

This is particularly helpful to detect change in state that could stop the services, so that the app can present UI to fix it (grant permissions, turn on Bluetooth, etc).

Possible values reported in  `RxBleClientStateListener.onStateChanged`  are:

-   `BLUETOOTH_NOT_AVAILABLE`: Bluetooth is not available on this device.
-   `BLUETOOTH_NOT_ENABLED`: Bluetooth is not enabled on the device.
-   `LOCATION_PERMISSION_NOT_GRANTED`: Location permissions have not been granted for the app.
-   `LOCATION_SERVICES_NOT_ENABLED`: Location Services are disabled on this device.
-   `READY`: BLE client is operational.

