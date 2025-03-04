package com.bambuser.commerce_sdk_demo_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CommerceSDKDemoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        var text by remember { mutableStateOf("") }

                        Column(modifier = Modifier.padding(16.dp)) {
                            TextField(
                                value = text,
                                onValueChange = { newText ->
                                    text = newText
                                },
                                label = { Text("Enter show id") },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                if (text.isBlank()) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Show id is empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    startActivity(
                                        Intent(this@MainActivity, LiveActivity::class.java).apply {
                                            putExtra("eventId", text)
                                        }
                                    )
                                }
                            }) {
                                Text("Show Live Video")
                            }
                        }
                    }
                }
            }
        }
    }
}
