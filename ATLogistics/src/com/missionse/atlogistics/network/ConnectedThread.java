package com.missionse.atlogistics.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class ConnectedThread extends Thread {

	private BluetoothService bluetoothService;
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;

	public ConnectedThread(final BluetoothService service, final BluetoothSocket socket) {
		bluetoothService = service;
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the BluetoothSocket input and output streams
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;

		// Keep listening to the InputStream while connected
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);

				// Send the obtained bytes to the UI Activity
				bluetoothService.onIncomingData(bytes, buffer);

			} catch (IOException e) {
				bluetoothService.onConnectionLost();
				break;
			}
		}
	}

	public void write(final byte[] buffer) {
		try {
			mmOutStream.write(buffer);

			// Share the sent message back to the UI Activity
			bluetoothService.onOutgoingData(buffer);
		} catch (IOException e) {
		}
	}

	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}
