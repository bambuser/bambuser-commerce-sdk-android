package com.bambuser.commerce_sdk_demo_app.experiences

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bambuser.commerce_sdk_demo_app.shoppablevideos.CardContainer
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme

class ExperiencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CommerceSDKDemoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                    ) {

                        CardContainer(
                            mainText = "Horizontal List experience",
                            subText = "View a list of shoppable videos in a horizontal autoplay list",
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@ExperiencesActivity,
                                        AutoplayListActivity::class.java,
                                    )
                                )
                            }
                        )


                        Spacer(modifier = Modifier.height(16.dp))

                        CardContainer(
                            mainText = "TikTok like experience",
                            subText = "Fetch and display shoppable video list in a vertical feed",
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@ExperiencesActivity,
                                        VerticalFeedActivity::class.java,
                                    )
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            }
        }
    }
}