pi@raspberrypi:~$ sudo bluetoothctl
[NEW] Controller 00:1B:10:00:2A:EC raspberrypi [default]
[NEW] Device 00:16:53:13:EF:A9 NXT
[bluetooth]# scan on
Discovery started
[CHG] Controller 00:1B:10:00:2A:EC Discovering: yes
[CHG] Device 00:16:53:13:EF:A9 LegacyPairing: yes
[CHG] Device 00:16:53:13:EF:A9 RSSI: -67
[CHG] Device 00:16:53:13:EF:A9 RSSI: -76
[bluetooth]# pair 00:16:53:13:EF:A9
Attempting to pair with 00:16:53:13:EF:A9
Failed to pair: org.bluez.Error.AuthenticationTimeout
[bluetooth]#
[bluetooth]# pair 00:16:53:13:EF:A9
Attempting to pair with 00:16:53:13:EF:A9
Failed to pair: org.bluez.Error.AuthenticationTimeout
[CHG] Device 00:16:53:13:EF:A9 RSSI: -86
[CHG] Device 00:16:53:13:EF:A9 RSSI: -77
[bluetooth]# pair 00:16:53:13:EF:A9
Attempting to pair with 00:16:53:13:EF:A9
[bluetooth]# 1234

Invalid command
[bluetooth]# agent on
Agent registered
[bluetooth]# agent
Missing on/off/capability argument
[bluetooth]# agent
DisplayOnly      KeyboardDisplay  NoInputNoOutput  on
DisplayYesNo     KeyboardOnly     off
[bluetooth]# agent
DisplayOnly      KeyboardDisplay  NoInputNoOutput  on
DisplayYesNo     KeyboardOnly     off
[bluetooth]# agent DisplayYesNo
Agent is already registered
Failed to pair: org.bluez.Error.AuthenticationTimeout
[bluetooth]# agent DisplayYesNo on
Invalid argument DisplayYesNo on
[bluetooth]# agent DisplayYesNo
Agent is already registered
[bluetooth]# agent off
Agent unregistered
[bluetooth]# agent DisplayYesNo
Agent registered
[bluetooth]# pair 00:16:53:13:EF:A9
Attempting to pair with 00:16:53:13:EF:A9
Request PIN code
[agent] Enter PIN code: 1234
[CHG] Device 00:16:53:13:EF:A9 Connected: yes
[bluetooth]# trust 00:16:53:13:EF:A9
Changing 00:16:53:13:EF:A9 trust succeeded
[bluetooth]# devices 00:16:53:13:EF:A9
Device 00:16:53:13:EF:A9 NXT
[CHG] Device 00:16:53:13:EF:A9 RSSI: -86
[bluetooth]# quit
Agent unregistered
[DEL] Controller 00:1B:10:00:2A:EC raspberrypi [default]


###############################################
## resumé
###############################################
pi@raspberrypi:~$ sudo bluetoothctl

[bluetooth]# scan on
[CHG] Device 00:16:53:13:EF:A9 RSSI: -67

[bluetooth]# agent off
Agent unregistered

[bluetooth]# agent DisplayYesNo
Agent registered

[bluetooth]# pair 00:16:53:13:EF:A9
Attempting to pair with 00:16:53:13:EF:A9
Request PIN code
[agent] Enter PIN code: 1234
[CHG] Device 00:16:53:13:EF:A9 Connected: yes

[bluetooth]# trust 00:16:53:13:EF:A9
Changing 00:16:53:13:EF:A9 trust succeeded

[bluetooth]# quit

