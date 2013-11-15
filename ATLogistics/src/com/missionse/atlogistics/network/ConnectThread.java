package com.missionse.atlogistics.network;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * This thread runs while attempting to make an outgoing connection with a device. It runs straight through; the
 * connection either succeeds or fails.
 */
public class ConnectThread extends Thread {

	private BluetoothService bluetoothService;

	private BluetoothSocket socket;
	private final BluetoothDevice device;

	public ConnectThread(final BluetoothService service, final BluetoothDevice device, final boolean secure) {
		bluetoothService = service;
		this.device = device;

		try {
			if (secure) {
				socket = device.createRfcommSocketToServiceRecord(BluetoothService.MY_UUID_SECURE);
			} else {
				socket = device.createInsecureRfcommSocketToServiceRecord(BluetoothService.MY_UUID_INSECURE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			// This is a blocking call and will only return on a
			// successful connection or an exception
			socket.connect();
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			bluetoothService.onConnectionFailed();
			return;
		}

		// Start the connected thread
		bluetoothService.onConnectionSuccessful(socket, device);
	}

	public void cancel() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
