# HololensGPS

These projects show how to setup and pass GPS position data using Bluetooth LE from Android to either a UWP application or directly to Hololens. The samples can easily be modified to send other types of data that you might want to pass from your phone such as touch based movement and input.

Based upon the great article at:
https://www.gamedev.net/articles/programming/general-and-gameplay-programming/gps-on-the-microsoft-hololens-r4497

## Key Files
* \Unity\ - Unity Hololens project for receiving Bluetooth LE messages. See below for build instructions.
* \UWPBluetoothReceiver\ - Contains a simple UWP app for receiving Bluetooth LE messages
* \AndroidClient\ - Android client app for sending out Bluetooth LE messages with GPS information

## Steps to build
* Pair your Android and target devices.
* Load and deploy the Android client.
* Run the UWP client to see Android -> UWP
* Load Unity and build a Universal Windows App to \UnityUWPBuild\. Deploy to Hololens to see Android -> Hololens in action.

