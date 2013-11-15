package com.missionse.atlogistics.network;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class AcceptThread extends Thread {

	// The local server socket
	private BluetoothService bluetoothService;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothServerSocket serverSocket;

	public AcceptThread(final BluetoothService service, final BluetoothAdapter adapter, final boolean secure) {
		bluetoothService = service;
		bluetoothAdapter = adapter;

		// Create a new listening server socket
		try {
			if (secure) {
				serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothService.NAME_SECURE,
						BluetoothService.MY_UUID_SECURE);
			} else {
				serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
						BluetoothService.NAME_INSECURE, BluetoothService.MY_UUID_INSECURE);
			}
		} catch (IOException e) {
			//We have a problem
		}
	}

	@Override
	public void run() {
		BluetoothSocket socket = null;

		// Listen to the server socket if we're not connected
		while (socket == null) {
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				socket = serverSocket.accept();
			} catch (Exception e) {
				break;
			}
		}

		// If a connection was accepted
		if (socket != null) {
			bluetoothService.onConnectionAccepted(socket, socket.getRemoteDevice());
		}

	}

	public void cancel() {
		try {
			serverSocket.close();
		} catch (Exception e) {
		}
	}
}
