# ARtisticGX

ARtisticGX is a kotlin based application, that uses the jetpack compose toolkit together with AR. With this application, you can use AR to place furniture or paintings on different surfaces and walls.

## Libraries/features

- [Sceneview](https://github.com/SceneView/sceneview-android/tree/main) to create ARscene composables and functions for hosting and resolving cloud anchors.
- CameraX for using the device's camera in the app.
- Accompanist library for jetpack compose to request permissions from the user.
- Zxing library for barcode scanner.
- Room for saving frame and model data locally.
- LiveData to read the data from DB and update views automatically when data changes.

## ARCore API authorization

The application uses ARCore API to host and resolve cloud anchors and requires an API key to connect to the API. The API key will be provided by us and must be put in the manifest file inside the meta-data tag to authorize connection.
```kotlin
<application
        android:name=".MyApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.ARtisticGX"
        tools:targetApi="31"
        tools:ignore="GoogleAppIndexingWarning">
        <!-- "AR Required" app, requires "Google Play Services for AR" (ARCore)
     to be installed, as the app does not include any non-AR features. -->

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.ARtisticGX">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <!-- Tells our app to use this API key to connect to the google cloud project
             API key goes to the currently empty value-->
        <meta-data
            android:name="com.google.android.ar.API_KEY"
            android:value=""/>
    </application>
```

## Known problems

- There are times when resolving the cloud anchors for furniture doesn't work even though saving the cloud anchors was successful. If this problem happens, please try to find a different surface and try saving the cloud anchor for the furniture again.
- If the user presses the device's back button to navigate to the last view while in the furniture/paintings list view, it brings the user to the QR camera view as intended but the camera is completely black. Pressing the AR button to go to the AR camera view and then back to QR fixes this issue.
