using System;
public class GPS_DataPacket
{
    public double Latitude;
    public double Longitude;
    public float Heading;
    public float Speed;
    public static GPS_DataPacket ParseDataPacket(byte[] data)
    {
        GPS_DataPacket gps_Data = new GPS_DataPacket();
        gps_Data.Latitude = BitConverter.ToDouble(data, 0);
        gps_Data.Longitude = BitConverter.ToDouble(data, 8);
        gps_Data.Heading = BitConverter.ToSingle(data, 16);
        gps_Data.Speed = BitConverter.ToSingle(data, 20);
        return gps_Data;
    }
    public override string ToString()
    {
        string lat, lng;
        if (Latitude > 0)
        {
            lat = string.Format("{0:0.00} AfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfA'A?N", Latitude);
        }
        else
        {
            lat = string.Format("{0:0.00} AfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfA'A?S", -Latitude);
        }
        if (Longitude > 0)
        {
            lng = string.Format("{0:0.00} AfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfA'A?E", Longitude);
        }
        else
        {
            lng = string.Format("{0:0.00} AfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfA'A?W", -Longitude);
        }
        return string.Format("Latitude: {0}, Longitude: {1}, Heading: {2:0.00}AfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfAfA'A?, Speed: {3:0.00} knots", lat, lng, Heading, Speed);
    }
}