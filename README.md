# esim-sdk-android 1Global
## Setup maven repository
### Automatic setup via GitHub Packages
in your root folder add our repo as a maven repository

settings.gradle
```gradle
maven {
    url "https://maven.pkg.github.com/my1global/esim-android-sdk"

    credentials {
        username = "GITHUB_USER" // Your GitHub username
        password = "GITHUB_USER_TOKEN" // Your GitHub token
    }
}
```

settings.gradle.kts
```kotlin
maven {
        url = uri("https://maven.pkg.github.com/my1global/esim-android-sdk")

        credentials {
            username = "GITHUB_USER" // Your GitHub username
            password = "GITHUB_USER_TOKEN" // Your GitHub token
        }
}
```

### Manual setup

If you don’t want to add the GitHub Packages repository, you can download the AAR directly and include it in your project.

1) Download the artifact

Grab the latest .aar from the package page:
https://github.com/my1global/esim-android-sdk/packages/2406778

2) Add the AAR to your project
	1.	In Android Studio, create a folder named libs inside your app module (usually app/libs/).
	2.	Copy the downloaded esim-android-sdk-<version>.aar into that libs folder.
	3.	Refresh/Sync Gradle when prompted.

3) Declare the dependency

You can do this in one of two ways. The simplest is to reference the file directly (no extra repository config needed).

Option A: File reference (recommended)

app/build.gradle (Groovy)
```gradle
dependencies {
    implementation(files("libs/esim-android-sdk-<version>.aar"))
}
```
app/build.gradle.kts (Kotlin DSL)
```kotlin
dependencies {
    implementation(files("libs/esim-android-sdk-<version>.aar"))
}
```
Option B: flatDir repository

app/build.gradle (Groovy)
```gradle
repositories {
    flatDir { dirs 'libs' }
}

dependencies {
    implementation(name: "esim-android-sdk-<version>", ext: "aar")
}
```
app/build.gradle.kts (Kotlin DSL)
```kotlin
repositories {
    flatDir { dirs("libs") }
}

dependencies {
    implementation(name = "esim-android-sdk-<version>", ext = "aar")
}
```
Replace <version> with the actual file’s version name you downloaded.

⸻

4) Sync and import

Sync the project. You can now import and use the SDK classes in your code as usual.


## Add package to your app

In your app folder add the package as a dependency

```gradle
dependencies {
    implementation "com.oneglobal:esim-sdk:1.1.4@aar"
}
```

```kotlin
dependencies {
    implementation("com.oneglobal:esim-sdk:1.1.4@aar")
}
```


## SDK Configuration

The SDK provides optional configuration parameters that can be set in the consuming application’s `AndroidManifest.xml`.
  
**Enable MMS Support**

You can control whether MMS settings should be added to the APN configuration. This configuration may be applied either during **eSIM setup** or through the SDK’s custom carrier service named `com.oneglobal.esim.sdk.OneGlobalCarrierService`.

**Parameter name:**

    com.oneglobal.esim.mmsSupport

**Accepted values:**

-  `"true"` → Enables MMS configuration

-  `"false"` → Disables MMS configuration (default)

If the parameter is omitted or has an invalid value, the SDK defaults to `"false"`. If the SDK encounters an internal exception, it will fall back to `"true"`.

  
When enabled (`"true"`), the following MMS configuration will be applied to the APN:

    MMSC = http://mmsc.truphone.com:1981/mm1
    MMSPORT = 1981
    TYPE = default,supl,dun,mms

**Manifest Example:**

```
<application ... >

	<meta-data

	android:name="com.oneglobal.esim.mmsSupport"

	android:value="false"  />

</application>
```  


## App signing

It is required that the app signing match with the esim profile in order to be able to use the esim installation or any carrier privileges. 

⚠️Your app signing SHA-1 should match the esim profile signing.⚠️

For example

```bash
keytool -list -v -keystore your-keystore.jks -alias your-alias
```
Your otuput should be similar to: 

```
Certificate fingerprints:
         SHA1:  AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12
         SHA256:  12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF
```

if the app bundle id was include on the esim profile it will restrict apps with correct signing that the bundle Id does not match.

## Usage

In order to initiate the class do as follow


```java
import android.os.Bundle;
import android.util.Log;
import androidx.activity.ComponentActivity;
import com.oneglobal.esim.sdk.EsimManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ComponentActivity {
    private final List<String> logsState = new ArrayList<>();
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();

        EsimManager esimManager = new EsimManager(this, null);
    }

    private void addLog(String message) {
        logsState.add(message);
        Log.d("EsimManager", message);
    }
}

```

```kotlin

import com.oneglobal.esim.sdk.EsimManager

class MainActivity : ComponentActivity() {
    private val logsState = mutableStateOf(listOf<String>())
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fun addLog(message: String) {
            logsState.value = logsState.value + message
            Log.d("EsimManager", message)
        }

        val esimManager = EsimManager(this, null)
     
     }
}
```

### haveCarrierPrivileges
Returns a boolean confirming if you have carrier privileges.
```kotlin
val privileges = esimManager.haveCarrierPrivileges()
```

### isEsimSupported
Returns a boolean that represents if the device support esim.
```kotlin
val esimSupport = esimManager.isEsimSupported()
```
### setupEsim
Returns a CompletableFuture<Boolean> that will return a boolean if it was able to install the esim. It will emit events with more information on why it fail when it fails.

```kotlin
val payload = "LPA:1\$rsp.truphone.com\$QRF-BETTERROAMING-PMRDGIR2EARDEIT5"
val future = esimManager.setupEsim(payload)
   future.thenAccept {
                Toast.makeText(context, "Esim setup: $it", Toast.LENGTH_SHORT).show()
            }.exceptionally {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                null
            }.thenRun {
                setLoading(false)
            }
```
### setAPN
This will set APN in the device. It will throw an exception if you don't have carrier privileges.
```kotlin

import com.oneglobal.esim.sdk.TitleAPN

  try {
     val success = esimManager.setAPN(TitleAPN.ONE_GLOBAL)
    Toast.makeText(context, "Esim APN fix: $success", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()    
    }

```
### getIccids
Returns an array of string with iccids from 1Global. Would throw an exception if you dont have carrier privileges
```kotlin
   try {
                val iccids = esimManager.iccids
                Toast.makeText(context, "Esim list: $iccids", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
```

### Event listener
You may optionally provide an implementation of this interface to intercept events.
```kotlin

 val esimManager = EsimManager(this) { eventType, message  ->
            addLog("$eventType" + if (message != null) ": $message" else "")
            if (eventType == EsimEventType.SETUP_ESIM_SHOW_PROMPT) {
                isLoading = true
            }
            if (eventType == EsimEventType.SETUP_ESIM_SUCCESS || eventType == EsimEventType.SETUP_ESIM_FAILED || eventType == EsimEventType.SETUP_ESIM_CANCELLED) {
                isLoading = false
            }
        }

```

### Retry Installation Flow

Sometimes the eSIM may be installed successfully, but the SDK returns an error. In such cases, it's recommended to implement a retry installation flow that checks if the eSIM is already installed before starting the installation process from the beginning.

**Why this happens:**

- Network interruptions during installation
- System delays in reporting installation status
- User interactions that interrupt the SDK's response handling

**Recommended approach:**
Before retrying the installation, check if the eSIM is already installed by verifying if the ICCID exists in the list of installed eSIMs with the `getIccids` method. Additionally, it is recommended to verify the installation status with the backend. If the eSIM has status `INSTALLED`, treat the installation as successful and continue the flow.

### Lifecycle Management

**Important:** The `EsimManager` requires proper lifecycle management to avoid issues when using it across multiple activities.

#### Proper Usage Pattern

**Java Example:**
```java
public class MainActivity extends ComponentActivity {
    private EsimManager esimManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the manager but don't start it yet
        esimManager = new EsimManager(this, (eventType, data -> {
            // Handle events
            Log.d("EsimManager", "Event: " + eventType + ", Data: " + data.toString());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the manager when activity becomes active
        if (!esimManager.isStarted()) {
            esimManager.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the manager when activity becomes inactive
        if (esimManager.isStarted()) {
            esimManager.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure cleanup when activity is destroyed
        if (esimManager.isStarted()) {
            esimManager.stop();
        }
    }
}
```

**Kotlin Example:**
```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var esimManager: EsimManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create the manager but don't start it yet
        esimManager = EsimManager(this) { eventType, message ->
            // Handle events
            Log.d("EsimManager", "Event: $eventType")
        }
    }

    override fun onResume() {
        super.onResume()
        // Start the manager when activity becomes active
        if (!esimManager.isStarted) {
            esimManager.start()
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop the manager when activity becomes inactive
        if (esimManager.isStarted) {
            esimManager.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure cleanup when activity is destroyed
        if (esimManager.isStarted) {
            esimManager.stop()
        }
    }
}
```

#### Lifecycle Methods

- **`start()`**: Registers the `BroadcastReceiver`. Call this in `onResume()` or when you need to start listening for eSIM events.
- **`stop()`**: Unregisters the `BroadcastReceiver`. Call this in `onPause()` or when you no longer need to listen for events.
- **`isStarted()`**: Returns `true` if the receiver is currently registered.
- **`getInstanceId()`**: Returns the unique instance ID for this `EsimManager` instance.

#### When to Register/Unregister

**Register (`start()`) in:**
- `onResume()` - Most common pattern
- `onCreate()` - If you need immediate event handling
- When starting an eSIM operation

**Unregister (`stop()`) in:**
- `onPause()` - Most common pattern  
- `onDestroy()` - To ensure cleanup
- When switching activities
- When no longer needing eSIM event handling

### Cleanup
After using the EsimManager object don't forget to call `stop()` to remove registered observers.
```
esimManager.stop()
```

## Automatic APN setup

As described in the [Android documentation](https://source.android.com/docs/core/connect/carrier#how_to_build_your_application), it is possible to configure the APN automatically during installation or when switching IMSI.

To support this, you need to provide a custom implementation of `android.service.carrier.CarrierService`. This service will allow you to define APN settings that apply when the eSIM is installed or an IMSI switch occurs.

### 1Global implementation

1. Just update your AndroidManifest.xml
    ```xml
    <application  .... >
        <service android:name="com.oneglobal.esim.sdk.OneGlobalCarrierService"
            android:exported="true"
            android:permission="android.permission.BIND_CARRIER_SERVICES">
            <intent-filter>
                <action android:name="android.service.carrier.CarrierService"/>
            </intent-filter>
        </service>
    ```

### Manual Implementation

1. **Create a Custom CarrierService Implementation**  
   Extend the `CarrierService` class and override the necessary methods to define your APN configuration.

   ```java
   public class CustomCarrierService extends CarrierService {
       @Override
       public void onLoadCarrierConfig(Bundle carrierConfig) {
           // Configure APN settings here
       }
   }
   ```
2. **Register Your CarrierService in AndroidManifest.xml**  
   Modify your AndroidManifest.xml file to declare your CarrierService implementation:
 
    ```xml
    <application ... >
        <service android:name="com.your-namespace.CustomCarrierService"
            android:exported="true"
            android:permission="android.permission.BIND_CARRIER_SERVICES">
            <intent-filter>
                <action android:name="android.service.carrier.CarrierService"/>
            </intent-filter>
        </service>
    </application>
    ```

## Know issues

* If the use press the hardware back button during the presentation on the OS prompt to install an esim. The task would not return an error.
* On [IMSI](https://en.wikipedia.org/wiki/International_mobile_subscriber_identity) change the apn config gets deleted, IMSI change happens when your network change for example: If you are in Japan and fly to USA. To avoid that follow the steps on [Automatic APN setup](#automatic-apn-setup)

## FAQ
