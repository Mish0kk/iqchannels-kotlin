package ru.iqchannels.example

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.iqchannels.sdk.app.IQChannels

class IQFirebaseMessagingService : FirebaseMessagingService() {

	companion object {
		private const val TAG = "iqchannels"
	}

	override fun onNewToken(token: String) {
		Log.d(TAG, "Refreshed token: $token")
		IQChannels.instance().setPushToken(token)
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		Log.d(TAG, "Message received: $remoteMessage")
		super.onMessageReceived(remoteMessage)
	}
}
