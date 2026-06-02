package com.bambuser.commerce_sdk_demo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import com.bambuser.commerce_sdk_demo_app.live.LiveDetailsScreen
import com.bambuser.commerce_sdk_demo_app.ui.theme.CommerceSDKDemoAppTheme

private sealed interface TopLevelRoute {
    val icon: Int
    val label: String
}

private data object Live : TopLevelRoute {
    override val icon = R.drawable.ic_live
    override val label = "Live"
}

private data object Feed : TopLevelRoute {
    override val icon = R.drawable.ic_feed
    override val label = "Feed"
}

private data object Wishlist : TopLevelRoute {
    override val icon = R.drawable.ic_wishlist
    override val label = "Wishlist"
}

private data object Cart : TopLevelRoute {
    override val icon = R.drawable.ic_cart
    override val label = "Cart"
}

private data class LiveDetail(val showId: String)

private val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(Live, Feed, Wishlist, Cart)

class FirstActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CommerceSDKDemoAppTheme {
                val topLevelBackStack = remember { TopLevelBackStack<Any>(Live) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            TOP_LEVEL_ROUTES.forEach { route ->
                                val isSelected = route == topLevelBackStack.topLevelKey
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { topLevelBackStack.addTopLevel(route) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(route.icon),
                                            contentDescription = route.label
                                        )
                                    },
                                    label = { Text(route.label) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavDisplay(
                        backStack = topLevelBackStack.backStack,
                        onBack = {
                            if (!topLevelBackStack.removeLast()) {
                                finish()
                            }
                        },
                        entryProvider = entryProvider {
                            entry<Live> {
                                LiveScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onNavigateToDetails = { showId ->
                                        topLevelBackStack.add(LiveDetail(showId))
                                    }
                                )
                            }
                            entry<LiveDetail>(
                                metadata = metadata {
                                    put(NavDisplay.TransitionKey) {
                                        slideInVertically(
                                            initialOffsetY = { it },
                                            animationSpec = tween(300)
                                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                                    }
                                    put(NavDisplay.PopTransitionKey) {
                                        EnterTransition.None togetherWith
                                                slideOutVertically(
                                                    targetOffsetY = { it },
                                                    animationSpec = tween(300)
                                                )
                                    }
                                    put(NavDisplay.PredictivePopTransitionKey) {
                                        EnterTransition.None togetherWith
                                                slideOutVertically(
                                                    targetOffsetY = { it },
                                                    animationSpec = tween(300)
                                                )
                                    }
                                }
                            ) { key ->
                                LiveDetailsScreen(
                                    showId = key.showId,
                                    modifier = Modifier.padding(innerPadding),
                                    onBack = {
                                        if (!topLevelBackStack.removeLast()) {
                                            finish()
                                        }
                                    },
                                    onNavigateToWishlist = {
                                        topLevelBackStack.addTopLevel(Wishlist)
                                    },
                                )
                            }
                            entry<Feed> {
                                FeedScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onNavigateToWishlist = {
                                        topLevelBackStack.addTopLevel(Wishlist)
                                    },
                                )
                            }
                            entry<Wishlist> {
                                WishlistScreen(Modifier.padding(innerPadding))
                            }
                            entry<Cart> {
                                CartScreen(Modifier.padding(innerPadding))
                            }
                        }
                    )
                }
            }
        }
    }
}

private class TopLevelBackStack<T : Any>(startKey: T) {

    private var topLevelStacks: LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set

    val backStack = mutableStateListOf(startKey)

    private fun updateBackStack() =
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }

    fun addTopLevel(key: T) {
        if (topLevelStacks[key] == null) {
            topLevelStacks[key] = mutableStateListOf(key)
        } else {
            topLevelStacks.apply {
                remove(key)?.let { put(key, it) }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast(): Boolean {
        val currentStack = topLevelStacks[topLevelKey] ?: return false
        if (currentStack.isEmpty()) {
            return false
        }
        val removedKey = currentStack.removeLastOrNull()
        if (removedKey != null) {
            topLevelStacks.remove(removedKey)
        }
        if (topLevelStacks.isEmpty()) {
            return false
        }
        val nextKey = topLevelStacks.keys.lastOrNull()
        if (nextKey == null) {
            return false
        }
        topLevelKey = nextKey
        updateBackStack()
        return true
    }
}
