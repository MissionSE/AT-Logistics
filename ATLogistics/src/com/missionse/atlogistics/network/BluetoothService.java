package com.missionse.atlogistics.network;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BluetoothService {

	// Unique UUID for this application
	public static final UUID MY_UUID_SECURE = UUID.fromString("c48bd398-4ca3-11e3-8e77-ce3f5508acd9");
	public static final UUID MY_UUID_INSECURE = UUID.fromString("c48bd87a-4ca3-11e3-8e77-ce3f5508acd9");

	// Message types sent from the BluetoothNetworkService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names for message packing
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Name for the SDP record when creating server socket
	public static final String NAME_SECURE = "ATLogisticsSecure";
	public static final String NAME_INSECURE = "ATLogisticsInsecure";

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;       // we're doing nothing
	public static final int STATE_LISTEN = 1;     // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3;  // now connected to a remote device

	// Member fields
	private BluetoothAdapter adapter;
	private final Handler handler;
	private AcceptThread secureAcceptThread;
	private AcceptThread mInsecureAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int serviceState;

	public BluetoothService(final Context context, final Handler handler) {
		this.handler = handler;
		adapter = BluetoothAdapter.getDefaultAdapter();
		setState(STATE_NONE);
	}

	private synchronized void setState(final int state) {
		serviceState = state;
		handler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	public synchronized int getState() {
		return serviceState;
	}

	public synchronized void start() {
		if (serviceState == BluetoothService.STATE_NONE) {
			restart();
		}
	}

	public synchronized void restart() {
		cancelConnectionThreads();

		// Start the thread to listen on a BluetoothServerSocket
		if (secureAcceptThread == null) {
			secureAcceptThread = new AcceptThread(this, adapter, true);
			secureAcceptThread.start();
		}
		if (mInsecureAcceptThread == null) {
			mInsecureAcceptThread = new AcceptThread(this, adapter, false);
			mInsecureAcceptThread.start();
		}

		setState(STATE_LISTEN);
	}

	private void cancelConnectionThreads() {
		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
	}

	public synchronized void connect(final BluetoothDevice device, final boolean secure) {

		this.cancelConnectionThreads();

		adapter.cancelDiscovery();

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(this, device, secure);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	public synchronized void disconnect() {
		restart();
	}

	public synchronized void stop() {
		cancelConnectionThreads();

		if (secureAcceptThread != null) {
			secureAcceptThread.cancel();
			secureAcceptThread = null;
		}

		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.cancel();
			mInsecureAcceptThread = null;
		}
		setState(STATE_NONE);
	}

	public boolean write(final byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (serviceState != STATE_CONNECTED) {
				return false;
			}
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
		return true;
	}

	public void onConnectionFailed() {
		// Send a failure message back to the Activity
		Message msg = handler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "Unable to connect device");
		msg.setData(bundle);
		handler.sendMessage(msg);

		// Start the service over to restart listening mode
		restart();
	}

	public void onConnectionLost() {
		// Send a failure message back to the Activity
		Message msg = handler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "Device connection was lost");
		msg.setData(bundle);
		handler.sendMessage(msg);

		// Start the service over to restart listening mode
		restart();
	}

	public void onConnectionAccepted(final BluetoothSocket socket, final BluetoothDevice remoteDevice) {
		if (serviceState == STATE_CONNECTING || serviceState == STATE_LISTEN) {
			onConnectionSuccessful(socket, remoteDevice);
		} else {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void onConnectionSuccessful(final BluetoothSocket socket, final BluetoothDevice remoteDevice) {
		//cancelConnectionThreads();

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one device
		if (secureAcceptThread != null) {
			secureAcceptThread.cancel();
			secureAcceptThread = null;
		}
		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.cancel();
			mInsecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(this, socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = handler.obtainMessage(MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(DEVICE_NAME, remoteDevice.getName());
		msg.setData(bundle);
		handler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	public void onIncomingData(final int bytes, final byte[] buffer) {
		handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
	}

	public void onOutgoingData(final byte[] buffer) {
		handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
	}
}
