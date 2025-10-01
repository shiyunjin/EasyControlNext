# This is a hard fork from original [https://github.com/daitj/Easycontrol](https://github.com/mingzhixian/Easycontrol) 

To Support Android 11+(pair code)

# Easycontrol Next
Remote control your android phone using another android phone, this uses scrcpy's server code which was modified by original author to work with this application. 

There are bunch of options you can change for a device while adding a device, remember to check those carefully.


**This has been tested to work on Android Phone (Android 9+) and Philips Android TV (based on Android 11 for TV) only if you are using Android device in your car, or VR headset like (Quest) or some other Android device it might change some screen awake time, screen resolution settings which can break the device**

To recover device's screen resolution
```
adb shell wm size RESOLUTION_WIDTH x RESOLUTION_HEIGHT
```

To fix screen off timeout
```
adb shell settings put system screen_off_timeout 60
```

Quest 3's default settings:
```
adb shell settings put system screen_off_timeout 86400000
```

I have changed the fork's package named from `com.daitj.easycontrolfork` to `com.shiyunjin.easycontrolnext` as not to have issues when both are installed. 

# Audit Report of original code

List of adb commands this application used are 

- To keep the device awake `settings put system screen_off_timeout 600000000`
- To change resolution of the device `wm size` 
- To check the state of the device after locking the screen `dumpsys deviceidle`
- To get details of the device display like resolution, density etc `dumpsys display`

Then to run the server in the device, it uses adb command to
- Delete `/data/local/tmp/easycontrolnext_*` if it exists
- Copy `easycontrolnext_server.jar` to `/data/local/tmp/easycontrolnext_server.jar`
  `easycontrolnext_server.jar` is built from `server` project as unsigned apk `server-release-unsigned.apk` and then copied over as `app/src/main/res/raw/easycontrolnext_server.jar`
- Shell command `app_process` is used to run server process
```
app_process -Djava.class.path=" + serverName + " / com.daitj.easycontrolnext.server.Server"
      + " serverPort=" + device.serverPort
      + " listenClip=" + (device.listenClip ? 1 : 0)
      + " isAudio=" + (device.isAudio ? 1 : 0)
      + " maxSize=" + device.maxSize
      + " maxFps=" + device.maxFps
      + " maxVideoBit=" + device.maxVideoBit
      + " keepAwake=" + (device.keepWakeOnRunning ? 1 : 0)
      + " supportH265=" + ((device.useH265 && supportH265) ? 1 : 0)
      + " supportOpus=" + (supportOpus ? 1 : 0)
      + " startApp=" + device.startApp + " \n").getBytes()));
```

If launching particular app only is used, these two extra adb shell command are used:
```
monkey -p " + Options.startApp + " -c android.intent.category.LAUNCHER 1
```

```
am display move-stack " + appStackId + " " + displayId
```

# Debug Build
In GNU/Linux
```
cd easycontrolnext
# build server
./gradlew assembleDebug -p server
# copy server to app
./gradlew copyDebug -p server
# build app
./gradlew assembleDebug
```
# Credits from original author
[ADB protocol description](https://github.com/cstyan/adbDocumentation) (the official document is really bad, thanks to the cstyan). 

[Scrcpy](https://github.com/Genymobile/scrcpy)
