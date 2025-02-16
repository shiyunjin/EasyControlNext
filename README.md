# This is a hard fork from original [https://github.com/mingzhixian/Easycontrol](https://github.com/mingzhixian/Easycontrol) 

I just removed the activation restriction and forced donation part of the code. 

If you love this app, donate to the original author(mingzhixian) and show your appreciation there.

I just wanted to build something myself because of the sensitive nature of the task (remote control) and didn't want app to fetch anything or check anything from other remote servers.

# Easycontrol
Remote control your android phone using another android phone, this uses scrcpy's server code which was modified by original author to work with this application. 

# Debug Build
In linux
```
cd easycontrol
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