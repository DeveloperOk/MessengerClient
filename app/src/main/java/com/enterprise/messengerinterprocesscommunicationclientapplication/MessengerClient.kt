package com.enterprise.messengerinterprocesscommunicationclientapplication

import android.app.Activity.BIND_AUTO_CREATE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import kotlin.concurrent.thread

class MessengerClient(val context: Context,
                      val packageOfServerApplication: String,
                      val packageAndClassOfServiceOfServerApplication: String,
                      val onReplyFromServer: (Context, String?) -> Unit) {

    val TAG = "MessengerClient"

    private val clientAppMessenger = Messenger(IncomingHandler())

    private var messageToSendServer: String = ""

    // Messenger IPC - Message Handler
    internal inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val receivedBundle = msg.data
            val receivedData = receivedBundle.getString(AppConstants.DATA)

            onReplyFromServer(context, receivedData)

            Log.d(TAG, "Test")

        }
    }

    val serviceConnection: ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val serverAppMessenger = Messenger(service)

            val message = Message.obtain(null, 0)
            val bundle = Bundle()
            bundle.putString(AppConstants.DATA, messageToSendServer)
            message.data = bundle

            message.replyTo=clientAppMessenger

            try {
                serverAppMessenger.send(message)
            }catch (exception: Exception){

                exception.printStackTrace()

            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {


        }


    }

    public fun sendMessageToServer(message: String){

        thread{

            messageToSendServer = message

            val messengerIntent = Intent()
            messengerIntent.component = ComponentName(packageOfServerApplication,
                packageAndClassOfServiceOfServerApplication)

            context.bindService(messengerIntent, serviceConnection, BIND_AUTO_CREATE)

        }

    }
}