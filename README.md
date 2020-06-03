
[![Actions Status](https://github.com/kike-canaries/android-hpma115s0/workflows/Android%20CI/badge.svg)](https://github.com/kike-canaries/android-hpma115s0/actions) [![Liberapay Status](http://img.shields.io/liberapay/receives/CanAirIO.svg?logo=liberapay)](https://liberapay.com/CanAirIO)

# CanAir.io Air quality Reporter 

<a href="https://play.google.com/store/apps/details?id=hpsaturn.pollutionreporter" target="_blank"><img src="https://github.com/kike-canaries/android-hpma115s0/blob/master/assets/googleplay/gplayicon.png" align="right" width="128" ></a>

[CanAirIO](https://canair.io) is a citizen science initiative for air quality tracking, visualization and dissemination by using PM2.5 particulate material sensors paired with your smartphone via bluetooth.

This code is for [CanAir.io](https://canair.io) Android app that using a reporter device [esp32-hpma115s0](https://github.com/kike-canaries/esp32-hpma115s0) pollution sensor or the `Beta` version with a [Panasonic PM sensor](https://github.com/hpsaturn/CanAirIO)

**Full guide:** [English](https://github.com/kike-canaries/esp32-hpma115s0/wiki/Official-Guide-(EN)) **|** [Spanish](https://github.com/kike-canaries/esp32-hpma115s0/wiki/Official-Guide-(ES))

---
<a href="https://github.com/kike-canaries/android-hpma115s0/blob/master/screenshots/main.jpg"><img src="https://github.com/kike-canaries/android-hpma115s0/blob/master/screenshots/main.jpg" align="right" width="512" ></a>
---

## TODO

- [X] BLE scanning and connecting 
- [X] BLE auto connect and reconnect
- [X] Receive data via BLE notification
- [X] Basic chart for PM 2.5 data
- [X] BLE persist connection on background service
- [X] Recoding data in the phone
- [X] List recorded tracks fragment
- [X] Open Street map fragment
- [X] Connect list records to record track
- [X] Connect Open Street Maps to pollution data
- [X] Firebase connection for publish reports
- [ ] Osmdroid clusters (for static points)
- [ ] Osmdroid routes (for line or dinamic points)
- [ ] Export data to json or others


# Installation

<a href="https://play.google.com/store/apps/details?id=hpsaturn.pollutionreporter" target="_blank"><img src="https://github.com/kike-canaries/android-hpma115s0/blob/master/assets/googleplay/gplayicon.png" align="right" width="128" ></a>

You can install it from the [release section](https://github.com/kike-canaries/esp32-hpma115s0/releases) downloading the last signed apk or installing from [GooglePlay](https://play.google.com/store/apps/details?id=hpsaturn.pollutionreporter)

# Building

## Dependencies

- Android SDK
- CMake

## Requirements

Please first clone the project with all submodules:

```bash
git clone --recursive https://github.com/kike-canaries/android-hpma115s0.git
```

### Firebase

This application uses a Firebase Database instance to store mobile air quality reports, 

For local development, you will need to create a database in the [Firebase Console](https://console.firebase.google.com/) using `hpsaturn.pollutionreporter` as the application identifier and retrieve a `google-services.json` file. See instructions [here](https://support.google.com/firebase/answer/7015592?hl=en).

After that copy this file into the project:

```bash
cd android-hpma115s0.git && cp ~/google-services.json app/
```

### Aqicn API key

Please put your Aqicn API key in `app/src/main/res/values/api_aqicn.xml` or create a fake file like with:

``` xml
<resources>
    <string name="api_aqicn_key">7cbbbb864b9c0755b8xxxxyyy</string>
</resources>
```

## Compiling

```bash
./gradlew assembleDebug
```

# Configuration

Please see the firmware [documentation](https://github.com/kike-canaries/esp32-hpma115s0/wiki/Official-Guide-(EN)#using-canairio-app) for using and configure your device.

# Credits

<div>Icons made by <a href="https://www.flaticon.com/authors/prosymbols" title="Prosymbols">Prosymbols</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
