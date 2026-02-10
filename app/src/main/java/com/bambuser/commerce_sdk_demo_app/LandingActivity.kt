package com.bambuser.commerce_sdk_demo_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bambuser.commerce_sdk_demo_app.shoppablevideos.SingleShoppableVideoActivity

class LandingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.padding(16.dp)) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    val intent =
                        Intent(this@LandingActivity, SingleShoppableVideoActivity::class.java)
                    Button(onClick = {
                        intent.putExtra("videoId", "puv_7mMdXmSJQn9V3ByMBHJ7z1")
                        startActivity(intent)
                    }) {
                        Text("Video 1")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        intent.putExtra("videoId", "puv_kTjG3APuhjpNXKVQondtJj")
                        startActivity(intent)
                    }) {
                        Text("Video 2")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        intent.putExtra("videoId", "puv_ZJjpBNmpz35jDaTQfzaA2i")
                        startActivity(intent)
                    }) {
                        Text("Video 3")
                    }
                }
            }
        }
    }
}