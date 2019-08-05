package org.shadowrunrussia2020.android

import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.character.CharacterRepository
import org.shadowrunrussia2020.android.character.CharacterWebService
import org.shadowrunrussia2020.android.qr.showInfoMessage

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    private val TAG = "FirebaseMessaging"

    private val mRepository by lazy {CharacterRepository(
        (application as ShadowrunRussia2020Application).getRetrofit().create(CharacterWebService::class.java),
        (application as ShadowrunRussia2020Application).getDatabase().characterDao()
    )}

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(TAG, "From: ${remoteMessage?.from}")

        CoroutineScope(Dispatchers.IO).launch { mRepository.refresh() }

        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        remoteMessage?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            CoroutineScope(Dispatchers.Main).launch {
                showInfoMessage(applicationContext, "${it.title}. ${it.body}")
            }
        }
    }

}
