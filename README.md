# steps-meter 

**steps-meter** count your walking step and runing meter.

*by Muzaffer Pashazade, 2018*

# Installation

## Gradle

```bash
implementation 'com.google.android.gms:play-services:12.0.1'
```

## AndroidManifest

```bash
        <uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
```   

```bash
        <activity android:name="com.muzafferus.pedometer.Pedometer" />
        <service android:name="com.muzafferus.pedometer.CountingService" />
        <service android:name="com.muzafferus.pedometer.ActivityRecognizedService" />
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
long stepWalk = pedometer.getStepWalking();
float distanceRun = pedometer.getStepRunning();
```

If you want to using SharedPreferences:

```bash
sharedPreferences.getInt("numWalkSteps", 0)
sharedPreferences.getFloat("numRunDistance", 0)
```

