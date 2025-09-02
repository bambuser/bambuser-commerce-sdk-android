package com.bambuser.commerce_sdk_demo_app.shoppablevideos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bambuser.commerce_sdk_demo_app.R
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme

class ShoppableVideosContainerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CommerceSDKDemoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Companion.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                    ) {

                        CardContainer(
                            mainText = "Single shoppable video",
                            subText = "Fetch and display single shoppable video",
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@ShoppableVideosContainerActivity,
                                        SingleShoppableVideoActivity::class.java,
                                    )
                                )
                            }
                        )


                        Spacer(modifier = Modifier.Companion.height(16.dp))

                        CardContainer(
                            mainText = "Shoppable video - Playlist",
                            subText = "Fetch and display single shoppable video playlist",
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@ShoppableVideosContainerActivity,
                                        PlaylistShoppableVideoActivity::class.java,
                                    )
                                )
                            }
                        )

                        Spacer(modifier = Modifier.Companion.height(16.dp))

                        CardContainer(
                            mainText = "Shoppable video - SKU",
                            subText = "Fetch and display single shoppable videos bind to a product SKU",
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@ShoppableVideosContainerActivity,
                                        SKUShoppableVideoActivity::class.java,
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardContainer(
    mainText: String,
    subText: String,
    onClick: () -> Unit,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(8f)
                    .padding(16.dp),
            ) {
                Text(
                    text = mainText,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subText,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Image(
                painter = painterResource(id = R.drawable.ic_arrow),
                contentDescription = "arrow",
                modifier = Modifier.weight(2f),
            )
        }
    }
}