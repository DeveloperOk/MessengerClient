package com.enterprise.messengerinterprocesscommunicationclientapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.enterprise.messengerinterprocesscommunicationclientapplication.ui.theme.MessengerInterProcessCommunicationClientApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    //Launching New Activity from Service works up to (including) Android 10
    //Android Messenger Inter Process Communication is tested up to (including) Android 15 and works
    //up to (including) Android 15
    //Android Messenger Inter Process Communication
    //Related Links
    //https://proandroiddev.com/ipc-techniques-for-android-messenger-3e8555a32167
    //https://www.youtube.com/watch?v=58GuJV46Exc
    //https://www.youtube.com/watch?v=Fa6zjujzoW8
    //https://www.youtube.com/watch?v=WWpi6U4GcGI
    //https://www.javacodegeeks.com/2014/01/android-bound-service-ipc-with-messenger.html
    //https://stackoverflow.com/questions/75741759/android-aidl-ipc-unable-to-call-service

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerInterProcessCommunicationClientApplicationTheme {

                AppMessengerClient()

            }
        }

    }

}



@Composable
fun AppMessengerClient() {
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){

            MainBody()

        }
    }
}

@Composable
fun MainBody() {

    var inputValue = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val context = LocalContext.current

    val textToDisplay = rememberSaveable {mutableStateOf("")}

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(text = stringResource(id = R.string.main_activity_inter_process_communication))
        Text(text = stringResource(id = R.string.main_activity_messenger_client))

        Text(textToDisplay.value, color = Color.Green)

        OutlinedTextField(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            value = inputValue.value,
            onValueChange = {
                inputValue.value = it
            },
            label = {
                Text(text = stringResource(id = R.string.main_activity_value_to_send))
            }

        )

        Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            onClick = {

                val messengerClient = MessengerClient(context = context,
                    packageOfServerApplication = AppConstants.packageOfServerApplication,
                    packageAndClassOfServiceOfServerApplication = AppConstants.packageAndClassOfServiceOfServerApplication,
                    onReplyFromServer = { inputContext, message ->

                        textToDisplay.value = context.getString(R.string.main_activity_received) +
                                context.getString(R.string.main_activity_space) +
                                message.toString()

                    })

                messengerClient.sendMessageToServer(inputValue.value.text.toString())


            }) {

            Text(text = stringResource(id = R.string.main_activity_send))

        }

    }
}

