package com.example.bluetoothclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

public class Client extends Thread {
	private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mBluetoothAdapter;
    private final UUID uuid;
    public Client()
	{
		uuid = UUID.fromString("bb2cf8c0-bcc6-11e3-b1b6-0800200c9a66");
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		mmDevice = getDevice();
		
		BluetoothSocket tmp = null;
		
	    try {
	        // MY_UUID is the app's UUID string, also used by the server code
	        tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
	    } catch (IOException e) { }
	    mmSocket = tmp;
	}
	
	private BluetoothDevice getDevice()
	{
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


		if (pairedDevices.size() > 0) {
		
		    for (BluetoothDevice device : pairedDevices) {  	
		    	
		    	ParcelUuid[] list = device.getUuids();
		    	
		    	for (ParcelUuid uuid : list)
		    	{
		    		if( uuid.getUuid() == this.uuid )
			    		return device;
		    	}
		    }
		}
		return mmDevice;
	}
	
	public void run()
	{
		mBluetoothAdapter.cancelDiscovery();

		try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
	}
	
	public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
	
	public String getInput()
	{
		String text = "";
		try {
			InputStream stream = mmSocket.getInputStream();
			byte buffer[] = new byte[4096];
			stream.read(buffer);
			text = buffer.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return text;
	}
}
