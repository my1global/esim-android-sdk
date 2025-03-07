# esim-sdk-android 1Global
## Setup maven repository
in your root folder add our repo as a maven repository

settings.gradle
```gradle
maven {
    url "https://maven.pkg.github.com/Truphone/esim-android-sdk"

    credentials {
        username = "GITHUB_USER" // Your GitHub username
        password = "GITHUB_USER_TOKEN" // Your GitHub token
    }
}
```

settings.gradle.kts
```kotlin
maven {
        url = uri("https://maven.pkg.github.com/Truphone/esim-android-sdk")

        credentials {
            username = "GITHUB_USER" // Your GitHub username
            password = "GITHUB_USER_TOKEN" // Your GitHub token
        }
}
```

## Add package to your app

In your app folder add the package as a dependency

```gradle
dependencies {
    implementation "com.oneglobal:esim-sdk:1.0.0@aar"
}
```

```kotlin
dependencies {
    implementation("com.oneglobal:esim-sdk:1.0.0@aar")
}
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

 val esimManager = EsimManager(this) { eventType ->
            addLog("$eventType")
            if (eventType == EsimEventType.SETUP_ESIM_SHOW_PROMPT) {
                isLoading = true
            }
            if (eventType == EsimEventType.SETUP_ESIM_SUCCESS || eventType == EsimEventType.SETUP_ESIM_FAILED || eventType == EsimEventType.SETUP_ESIM_CANCELLED) {
                isLoading = false
            }
        }

```

## Know issues

If the use press the hardware back button during the presentation on the OS prompt to install an esim. The task would not return an error.

## FAQ