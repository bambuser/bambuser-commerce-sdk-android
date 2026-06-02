package com.bambuser.commerce_sdk_demo_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.bambuser.commerce_sdk_demo_app.cart.CartItem
import com.bambuser.commerce_sdk_demo_app.cart.CartViewModel
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme
import java.util.Locale

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(),
) {
    val items by viewModel.items.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val currency by viewModel.currency.collectAsState()
    var showCheckoutAlert by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        if (items.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cart),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Add products from videos or wishlist.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(items, key = { it.id }) { item ->
                        CartRow(
                            item = item,
                            onIncrement = { viewModel.updateQuantity(item.id, 1) },
                            onDecrement = { viewModel.updateQuantity(item.id, -1) },
                            onRemove = { viewModel.remove(item.id) },
                        )
                    }
                }

                Surface(tonalElevation = 2.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Subtotal",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = formatCartPrice(subtotal, currency),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { showCheckoutAlert = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Checkout")
                        }
                    }
                }
            }
        }
    }

    if (showCheckoutAlert) {
        AlertDialog(
            onDismissRequest = { showCheckoutAlert = false },
            title = { Text("Congratulations 🎉") },
            text = { Text("Your order is on the way!") },
            confirmButton = {
                TextButton(onClick = {
                    showCheckoutAlert = false
                    viewModel.clearAll()
                }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
private fun CartRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onRemove()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = item.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (!item.sizeName.isNullOrEmpty()) {
                        Text(
                            text = "· ${item.sizeName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = formatCartPrice(item.unitPrice, item.currency),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (item.original != null && item.original > item.unitPrice) {
                        Text(
                            text = formatCartPrice(item.original, item.currency),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Qty: ${item.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                        Text("−", style = MaterialTheme.typography.titleMedium)
                    }
                    IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                        Text("+", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatCartPrice(item.lineTotal, item.currency),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

private fun formatCartPrice(amount: Double, currency: String): String {
    return String.format(Locale.US, "%.2f %s", amount, currency)
}

@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {
    CommerceSDKDemoAppTheme {
        CartScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemPreview() {
    val item = CartItem(
        id = "123",
        title = "Cotton T-Shirt",
        brand = "Fashion Brand",
        unitPrice = 25.0,
        original = 35.0,
        quantity = 2,
        currency = "USD",
        imageUrl = "https://example.com/image.jpg",
        sizeName = "Medium",
    )
    CommerceSDKDemoAppTheme {
        CartRow(
            item = item,
            onIncrement = {},
            onDecrement = {},
            onRemove = {},
        )
    }
}
