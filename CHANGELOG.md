## esim-android-sdk-3.1.0.aar - 2026-02-09

### Changed
- AndroidManifest update: added dedicated process for `OneGlobalCarrierService`.

```diff
diff --git a/example-app/src/main/AndroidManifest.xml b/example-app/src/main/AndroidManifest.xml
index d150596..b8d6087 100644
--- a/example-app/src/main/AndroidManifest.xml
+++ b/example-app/src/main/AndroidManifest.xml
@@ -16,7 +16,8 @@
            <service android:name="com.betterroaming.OneGlobalCarrierService"
           android:exported="true"
           android:label="apn"
-          android:permission="android.permission.BIND_CARRIER_SERVICES">
+          android:permission="android.permission.BIND_CARRIER_SERVICES"
+          android:process=":one_global_carrier_service">
```
### Impact
- Improved service isolation and stability.
