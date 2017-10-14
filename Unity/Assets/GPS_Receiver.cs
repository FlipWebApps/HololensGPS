using UnityEngine;
#if NETFX_CORE
using Windows.Devices.Bluetooth.Advertisement;
using System.Runtime.InteropServices.WindowsRuntime;
#endif

public class GPS_Receiver : MonoBehaviour
{
#if NETFX_CORE
    BluetoothLEAdvertisementWatcher watcher;
    public static ushort BEACON_ID = 1775;
#endif
    public EventProcessor eventProcessor;

    void Awake()
    {
#if NETFX_CORE
        watcher = new BluetoothLEAdvertisementWatcher();
        var manufacturerData = new BluetoothLEManufacturerData
        {
        CompanyId = BEACON_ID
        };
        watcher.AdvertisementFilter.Advertisement.ManufacturerData.Add(manufacturerData);
        watcher.Received += Watcher_Received;
        watcher.Start();
#endif
    }
#if NETFX_CORE
    private async void Watcher_Received(BluetoothLEAdvertisementWatcher sender, BluetoothLEAdvertisementReceivedEventArgs args)
    {
        ushort identifier = args.Advertisement.ManufacturerData[0].CompanyId;
        byte[] data = args.Advertisement.ManufacturerData[0].Data.ToArray();
        // Updates to Unity UI don't seem to work nicely from this callback so just store a reference to the data for later processing.
        eventProcessor.QueueData(data);
    }
#endif
}