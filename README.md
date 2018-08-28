# steps-meter 

**steps-meter** count your walking step and runing meter.

*by Muzaffer Pashazade, 2018*

# Installation

## AndroidManifest

```bash
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
...

        <activity android:name="com.crocusoft.muzafferus.teststeps.Pedometer" />
        <service android:name="com.crocusoft.muzafferus.teststeps.CountingService" />
        <service android:name="com.crocusoft.muzafferus.teststeps.ActivityRecognizedService" />
```

## MainActivity

```bash
Pedometer pedometer = new Pedometer();
pedometer.start(height,
                manOrWoman,
                sharedPreferencesKey);

```

