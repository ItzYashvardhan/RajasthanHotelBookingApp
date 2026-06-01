package com.justlime.hotelbooking

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.justlime.hotelbooking.ui.hotel_booking.HotelBookingScreen
import com.justlime.hotelbooking.ui.hotel_details.HotelDetailsScreen
import com.justlime.hotelbooking.ui.hotel_details.HotelDetailsViewModel
import com.justlime.hotelbooking.ui.hotel_list.HotelListScreen
import com.justlime.hotelbooking.ui.hotel_list.HotelListViewModel
import com.justlime.hotelbooking.ui.onboarding.OnboardingScreen
import com.justlime.hotelbooking.ui.payment.PaymentErrorScreen
import com.justlime.hotelbooking.ui.payment.PaymentSuccessScreen
import com.justlime.hotelbooking.ui.theme.HotelBookingTheme
import com.justlime.hotelbooking.utils.AppConstant
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
    private var paymentAmount: Double = 0.0
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.ALPHA,
                1f, 0f
            )
            fadeOut.duration = 400L
            fadeOut.start()
        }

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            splashScreen.setKeepOnScreenCondition { mainViewModel.isLoading }
            HotelBookingTheme {
                navController = rememberNavController()
                val duration = 300
                if (mainViewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("Loading...")
                    }
                } else {

                    NavHost(
                        navController = navController,
                        startDestination = mainViewModel.startDestination,
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(duration)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(duration + 100)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(duration)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(duration + 100)
                            )
                        }
                    )
                    {
                        composable("onboarding") {
                            OnboardingScreen(
                                onGetStartedClick = {
                                    mainViewModel.completeOnboarding()
                                    navController.navigate("hotel_list") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("hotel_list") {

                            //ViewModel
                            val viewModel: HotelListViewModel = hiltViewModel()

                            //State
                            val exploreHotels = viewModel.pagedHotels.collectAsLazyPagingItems()
                            val favoriteHotels = viewModel.favoriteHotels.collectAsLazyPagingItems()
                            val searchQuery by viewModel.searchQuery.collectAsState()
                            val sortOrder by viewModel.sortOrder.collectAsState()

                            //Screen
                            HotelListScreen(
                                exploreHotels = exploreHotels,
                                favoriteHotels = favoriteHotels,
                                searchQuery = searchQuery,
                                sortOrder = sortOrder,
                                onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                                onSortOrderChanged = { viewModel.onSortOrderChanged(it) },
                                onFavoriteClick = { id, isFavorite ->
                                    viewModel.onFavoriteClick(id, isFavorite)
                                },
                                onNavigateToDetails = { hotelId -> navController.navigate("hotel_details/$hotelId") },
                                initialPage = 0
                            )
                        }

                        composable("hotel_details/{hotelId}") { _ ->

                            //ViewModel
                            val viewModel: HotelDetailsViewModel = hiltViewModel()

                            //State
                            val uiState by viewModel.uiState.collectAsState()
                            val scrollState = rememberScrollState()

                            //Screen
                            HotelDetailsScreen(
                                uiState = uiState,
                                scrollState,
                                onNavigateBack = { navController.popBackStack() },
                                onBookNowClick = { hotelId ->
                                    navController.navigate("hotel_booking/$hotelId")
                                },
                                onToggleFavorite = { id, isFavorite ->
                                    viewModel.setFavorite(id, isFavorite)
                                }
                            )
                        }

                        composable("hotel_booking/{hotelId}") { _ ->
                            val viewModel: HotelDetailsViewModel = hiltViewModel()

                            val uiState by viewModel.uiState.collectAsState()
                            val bookingState by viewModel.bookingState.collectAsState()
                            val infoDialogPlanState by viewModel.infoDialogPlan.collectAsState()
                            HotelBookingScreen(
                                uiState = uiState,
                                bookingState = bookingState,
                                infoDialogPlanState,
                                onNavigateBack = { navController.popBackStack() },
                                onRoomsChanged = { rooms, maxRooms ->
                                    viewModel.updateRooms(
                                        rooms,
                                        maxRooms
                                    )
                                },
                                onNightsChanged = { nights -> viewModel.updateNights(nights) },
                                onMealPlanSelected = { name, price ->
                                    viewModel.selectMealPlan(
                                        name,
                                        price
                                    )
                                },
                                onBookNowClick = { finalAmount ->
                                    paymentAmount = finalAmount
                                    startPayment(finalAmount)
                                },
                                onInfoClick = { planName -> viewModel.showInfoDialog(planName) },
                                onDismissDialog = { viewModel.dismissInfoDialog() }
                            )
                        }

                        composable("payment_success/{paymentId}") { backStackEntry ->
                            val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
                            PaymentSuccessScreen(
                                bookingId = paymentId,
                                onHomeClick = {
                                    navController.navigate("hotel_list") {
                                        popUpTo("hotel_list") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("payment_error/{errorMessage}") { backStackEntry ->
                            val errorMessage = backStackEntry.arguments?.getString("errorMessage")
                                ?: "Payment Failed"
                            PaymentErrorScreen(
                                errorMessage = errorMessage,
                                onRetryClick = {
                                    navController.popBackStack()
                                    startPayment(paymentAmount)
                                },
                                onBackClick = {
                                    navController.navigate("hotel_list") {
                                        popUpTo("hotel_list") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }

            }
        }
    }

    private fun startPayment(amountInRupees: Double) {
        val checkout = Checkout()
        checkout.setKeyID(AppConstant.RAZORPAY_TEST_KEY)

        try {
            val options = JSONObject()
            options.put("name", "Rajasthan Hotel Booking")
            options.put("description", "Room Reservation")
            options.put("theme.color", "#FF6F00")
            options.put("currency", "INR")
            options.put("amount", (amountInRupees * 100).toInt())
            val prefill = JSONObject()
            prefill.put("email", "test.user@example.com")
            prefill.put("contact", "9999999999")
            options.put("prefill", prefill)

            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing payment: ${e.message}", Toast.LENGTH_LONG)
                .show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {
        navController.navigate("payment_success/$paymentId")
    }

    override fun onPaymentError(errorCode: Int, response: String?, paymentData: PaymentData?) {
        navController.navigate("payment_error/${response ?: "Payment Failed"}")
    }
}