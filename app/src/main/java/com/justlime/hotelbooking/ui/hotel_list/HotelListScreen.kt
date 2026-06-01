package com.justlime.hotelbooking.ui.hotel_list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.justlime.hotelbooking.data.model.Hotel
import com.justlime.hotelbooking.ui.hotel_list.component.HotelCard
import com.justlime.hotelbooking.utils.mockHotels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    exploreHotels: LazyPagingItems<Hotel>,
    favoriteHotels: LazyPagingItems<Hotel>,
    searchQuery: String,
    sortOrder: SortOrder,
    onSearchQueryChanged: (String) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    initialPage: Int = 0
) {
    val exploreListState = rememberLazyListState() //TODO "Move state to route composable"
    val favoriteListState = rememberLazyListState()

    val keyboardController = LocalSoftwareKeyboardController.current

    var isManualRefresh by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    var previousSortOrder by rememberSaveable { mutableStateOf(sortOrder) }

    LaunchedEffect(sortOrder) {
        if (previousSortOrder != sortOrder) {
            previousSortOrder = sortOrder

            if (pagerState.currentPage == 0) {
                exploreListState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(exploreHotels.loadState.refresh) {
        if (exploreHotels.loadState.refresh !is LoadState.Loading) {
            isManualRefresh = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                isRefreshing = if (pagerState.currentPage == 0) isManualRefresh else false,
                state = pullToRefreshState,
                onRefresh = {
                    if (pagerState.currentPage == 0) {
                        isManualRefresh = true
                        exploreHotels.refresh()
                    }
                }
            )
    ) {
        Scaffold(
            topBar = {
                HeaderBar(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    keyboardController = keyboardController,
                    currentPage = pagerState.currentPage,
                    sortOrder = sortOrder,
                    onSortOrderChanged = onSortOrderChanged
                )
            },
            bottomBar = { BottomBar(pagerState, coroutineScope) }
        ) { paddingValues ->

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { page ->

                val currentHotels = if (page == 0) exploreHotels else favoriteHotels
                val isExplorePage = page == 0
                val currentListState = if (page == 0) exploreListState else favoriteListState

                val listUiState = when (currentHotels.loadState.refresh) {
                    is LoadState.Loading if currentHotels.itemCount == 0 -> "LOADING"
                    is LoadState.Error if currentHotels.itemCount == 0 -> "ERROR"
                    is LoadState.NotLoading if currentHotels.itemCount == 0 -> "EMPTY"
                    else -> "CONTENT"
                }

                AnimatedContent(
                    targetState = listUiState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                            animationSpec = tween(
                                300
                            )
                        )
                    },
                    label = "List State Animation"
                ) { targetState ->
                    when (targetState) {
                        "LOADING" -> {
                            if (isExplorePage && !isManualRefresh) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize())
                            }
                        }

                        "ERROR" -> {
                            val errorState = currentHotels.loadState.refresh as LoadState.Error
                            ErrorScreen(errorState, currentHotels)
                        }

                        "EMPTY" -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isExplorePage) "No hotels found" else "No favorite hotels yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        "CONTENT" -> {
                            HotelPagerList(
                                hotels = currentHotels,
                                listState = currentListState,
                                onNavigateToDetails = onNavigateToDetails,
                                onFavoriteClick = onFavoriteClick
                            )
                        }
                    }
                }
            }
        }

        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isManualRefresh,
            state = pullToRefreshState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    currentPage: Int,
    sortOrder: SortOrder,
    onSortOrderChanged: (SortOrder) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                },
                label = "Header Animation"
            ) { page ->
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (page == 0) "Explore Rajasthan Hotels" else "Saved Hotels",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (page == 0) "Find your perfect heritage stay" else "Your favorite picks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Slightly wider spacing for elegance
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SearchBar(searchQuery, onSearchQueryChanged, keyboardController)
                }

                SortDropdownMenu(
                    selectedOrder = sortOrder,
                    onOrderSelected = onSortOrderChanged
                )
            }
        }
    }
}

@Composable
private fun SortDropdownMenu(
    selectedOrder: SortOrder,
    onOrderSelected: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Sort",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOrder.entries.forEach { order ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = order.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedOrder == order) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedOrder == order) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onOrderSelected(order)
                        expanded = false
                    },
                    leadingIcon = {
                        if (selectedOrder == order) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Explore Screen") },
                label = { Text("Explore", fontWeight = FontWeight.Bold) },
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favourite Screen") },
                label = { Text("Favorite", fontWeight = FontWeight.Bold) },
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    keyboardController: SoftwareKeyboardController?
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                "Search hotels or locations...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
    )
}

@Composable
private fun HotelPagerList(
    hotels: LazyPagingItems<Hotel>,
    listState: LazyListState,
    onNavigateToDetails: (String) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(
            count = hotels.itemCount,
            key = hotels.itemKey { it.id }
        ) { index ->
            val hotel = hotels[index]
            if (hotel != null) {
                HotelCard(
                    hotel = hotel,
                    onClick = { onNavigateToDetails(hotel.id) },
                    onFavoriteClick = {
                        onFavoriteClick(
                            hotel.id,
                            hotel.isFavorite
                        )
                    }
                )
            }
        }

        when (val appendState = hotels.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = appendState.error.localizedMessage
                                ?: "Couldn't load more hotels",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = { hotels.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is LoadState.NotLoading -> {}
        }
    }
}

@Composable
private fun ErrorScreen(
    refreshState: LoadState.Error,
    hotels: LazyPagingItems<Hotel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = refreshState.error.localizedMessage ?: "Error loading data",
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { hotels.retry() }) {
            Text("Retry")
        }
    }
}

@Preview("HomeListScreen")
@Composable
fun HomeScreenPreview() {
    val mockHotelsFlow = flowOf(PagingData.from(mockHotels))
    val exploreHotels = mockHotelsFlow.collectAsLazyPagingItems()
    val favoriteHotels = mockHotelsFlow.collectAsLazyPagingItems()

    HotelListScreen(
        exploreHotels = exploreHotels,
        favoriteHotels = favoriteHotels,
        searchQuery = "",
        sortOrder = SortOrder.NONE,
        onSearchQueryChanged = {},
        onSortOrderChanged = {},
        onFavoriteClick = { _, _ -> },
        onNavigateToDetails = {},
        initialPage = 0
    )
}

@Preview("FavouriteScreen")
@Composable
fun FavouriteScreenPreview() {
    val mockHotelsFlow = flowOf(PagingData.from(mockHotels))
    val exploreHotels = mockHotelsFlow.collectAsLazyPagingItems()
    val favoriteHotels = mockHotelsFlow.collectAsLazyPagingItems()

    HotelListScreen(
        exploreHotels = exploreHotels,
        favoriteHotels = favoriteHotels,
        searchQuery = "",
        sortOrder = SortOrder.NONE,
        onSearchQueryChanged = {},
        onSortOrderChanged = {},
        onFavoriteClick = { _, _ -> },
        onNavigateToDetails = {},
        initialPage = 1
    )
}
