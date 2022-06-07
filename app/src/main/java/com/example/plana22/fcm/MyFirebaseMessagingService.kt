package com.example.plana22.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.plana22.Activities.introduction.SignUpActivity
import com.example.plana22.MainActivity
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        /* [START_EXCLUDE]
       *There are two types of messages data messages and notification messages. Data messages are handled
      here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
       traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
       is in the foreground. When the app is in the background an automatically generated notification is displayed.
       When the user taps on the notification they are returned to the app. Messages containing both notification
       and data payloads are treated as notification messages. The Firebase console always sends notification
      messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
       [END_EXCLUDE]
       */

        super.onMessageReceived(remoteMessage)

        // Handle FCM messages here.
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data Payload: ${remoteMessage.data}")

            val title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!

            sendNotification(title, message)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification body: ${it.body}")
        }

    }

    /**Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token : $token")

        /* If you want to send messages to this application instance or manage this
        apps subscriptions on the server side, send the Instance ID token to your
         app server.*/
        sendRegistrationToServer(token)

    }


    /** Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        // Implement this method to send token to your app server.
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(title: String, message: String){
        val intent = if (FireStoreClass().getCurrentUserId().isNotEmpty()){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, SignUpActivity::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,
            0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //The real notification builder using all the previousy set parameters above

        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        ).setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.

        if(Build.VERSION.SDK_INT >= O){
            val channel = NotificationChannel(
                channelId, "Channel Plana22",
                NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */,
            notificationBuilder.build())
    }



        companion object{
        private const val TAG = "MyFirebaseMsgService"
    }
}