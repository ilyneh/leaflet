package com.ilynehdev.leaflet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ilynehdev.feature.plants.PlantListScreen
import kotlinx.serialization.Serializable


@Serializable data object BrowseRoute
@Serializable data object SavedRoute

data class NavBarItem(
    val route: Any,
    val label: String
)

val NAV_ITEMS = listOf(
    NavBarItem(BrowseRoute, "Browse"),
    NavBarItem(SavedRoute, "Saved"),
)

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
            Column {
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
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            onClick = {
                                if (currentRoute != item.route) {
                                    backStack.clear()
                                    backStack.add(item.route)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(com.ilynehdev.feature.plants.R.drawable.ic_discover),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(item.label)
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
            onBack = {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            },
            entryProvider = entryProvider {
                entry<BrowseRoute> { PlantListScreen() }
            }
        )
    }
}
