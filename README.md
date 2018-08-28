# steps-meter 

**steps-meter** count your walking step and runing meter.

*by Muzaffer Pashazade, 2018*

# Installation

## Gradle

implementation 'com.google.android.gms:play-services:12.0.1'

## AndroidManifest

```bash
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
...

        <activity android:name="com.crocusoft.muzafferus.teststeps.Pedometer" />
        <service android:name="com.crocusoft.muzafferus.teststeps.CountingService" />
        <service android:name="com.crocusoft.muzafferus.teststeps.ActivityRecognizedService" />
```

## MainActivity

Using in onCreate method:

```bash
Pedometer pedometer = new Pedometer();
pedometer.start(height,
                manOrWoman,
                sharedPreferencesKey);

```

Get walk step and run distance:

```bash
double distanceRun = pedometer.getStepRunning();
long stepWalk = pedometer.getStepWalking();
```

