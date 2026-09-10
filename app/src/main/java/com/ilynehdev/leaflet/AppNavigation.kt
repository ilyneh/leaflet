package com.ilynehdev.leaflet

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ilynehdev.feature.plants.detail.PlantDetailScreen
import com.ilynehdev.feature.plants.list.PlantListScreen
import kotlinx.serialization.Serializable


private val SLIDE_SPEC = tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing)

@Serializable data object BrowseRoute
@Serializable data object SavedRoute
@Serializable data object PlantingsRoute
@Serializable data class PlantDetailRoute(val id: Long)

data class NavBarItem(
    val route: Any,
    @param:DrawableRes val iconRes: Int,
    val label: String
)

val NAV_ITEMS = listOf(
    NavBarItem(BrowseRoute, R.drawable.ic_browse_leaf,"Browse"),
    NavBarItem(SavedRoute, com.ilynehdev.feature.plants.R.drawable.ic_saved_bookmark,"Saved"),
    NavBarItem(PlantingsRoute, R.drawable.ic_plantings_pot, "Plantings")
)

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<Any>(BrowseRoute) }
    val currentRoute = backStack.lastOrNull() ?: BrowseRoute
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
        bottomBar = {
            if (currentRoute !is PlantDetailRoute) Column {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    NAV_ITEMS.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            ),
                            onClick = {
                                if (currentRoute != item.route) {
                                    backStack.clear()
                                    backStack.add(item.route)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(item.iconRes),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            onBack = {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            },
            entryProvider = entryProvider {
                entry<BrowseRoute> {
                    PlantListScreen(
                        onPlantClicked = { id -> backStack.add(PlantDetailRoute(id)) }
                    )
                }
                entry<PlantDetailRoute>(
                    metadata = NavDisplay.transitionSpec {
                        slideInHorizontally(SLIDE_SPEC, initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(SLIDE_SPEC, targetOffsetX = { -it / 4 })
                    } + NavDisplay.popTransitionSpec {
                        slideInHorizontally(SLIDE_SPEC, initialOffsetX = { -it / 4 }) togetherWith
                            slideOutHorizontally(SLIDE_SPEC, targetOffsetX = { it })
                    } + NavDisplay.predictivePopTransitionSpec {
                        slideInHorizontally(SLIDE_SPEC, initialOffsetX = { -it / 4 }) togetherWith
                            slideOutHorizontally(SLIDE_SPEC, targetOffsetX = { it })
                    },
                ) { route ->
                    PlantDetailScreen(
                        plantId = route.id,
                        onBackClicked = {
                            if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                        },
                    )
                }
                entry<SavedRoute> { PlaceholderScreen(title = "Saved") }
                entry<PlantingsRoute> { PlaceholderScreen(title = "Plantings") }
            }
        )
    }
}
