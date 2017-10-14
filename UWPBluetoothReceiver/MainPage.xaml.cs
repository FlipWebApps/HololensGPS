using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Devices.Bluetooth.Advertisement;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at https://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace BluetoothTest
{
    public sealed partial class MainPage : Page
    {
        BluetoothLEAdvertisementWatcher watcher;
        public static ushort BEACON_ID = 1775;

        public string Time { get; set; }
        public string Latitude { get; set; }
        public string Longitude { get; set; }

        public MainPage()
        {
            this.InitializeComponent();
            DataContext = this;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            Debug.WriteLine("Button_Click");

            if (watcher != null)
                watcher.Stop();
            watcher = new BluetoothLEAdvertisementWatcher();
            var manufacturerData = new BluetoothLEManufacturerData
            {
                CompanyId = BEACON_ID
            };
            watcher.AdvertisementFilter.Advertisement.ManufacturerData.Add(manufacturerData);
            watcher.Received += Watcher_Received;
            watcher.Start();
        }

        private async void Watcher_Received(BluetoothLEAdvertisementWatcher sender, BluetoothLEAdvertisementReceivedEventArgs args)
        {
            ushort identifier = args.Advertisement.ManufacturerData.First().CompanyId;
            byte[] data = args.Advertisement.ManufacturerData.First().Data.ToArray();
            var ignore = Dispatcher.RunAsync(CoreDispatcherPriority.Normal,
            () =>
            {
                var gpsData = GPS_DataPacket.ParseDataPacket(data);

                Debug.WriteLine(gpsData.ToString());
                Time = DateTime.Now.ToString();
                Latitude = gpsData.Latitude.ToString();
                Longitude = gpsData.Longitude.ToString();
                Bindings.Update();

                /* GPS Data Parsing / UI integration goes here */
            }
            );
        }
    }
}
