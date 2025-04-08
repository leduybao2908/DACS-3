package com.example.dacs3

import android.os.Bundle
import android.util.JsonToken
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.dacs3.data.AppDatabase
import com.example.dacs3.data.UserPreferences
import com.example.dacs3.navigation.*
import com.example.dacs3.ui.components.*
import com.example.dacs3.ui.screens.auth.*
import com.example.dacs3.ui.screens.profile.ProfileScreen
import com.example.dacs3.ui.theme.DACS3Theme
import com.example.dacs3.ui.viewmodels.*
import com.example.dacs3.ui.screens.chat.ChatScreen
import com.example.dacs3.ui.screens.videocall.VideoCallScreen
import android.util.Log
import com.stringee.StringeeClient
import com.stringee.call.StringeeCall
import com.stringee.call.StringeeCall2
import com.stringee.exception.StringeeError
import com.stringee.listener.StringeeConnectionListener
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var stringeeClient: StringeeClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val userPreferences = UserPreferences(this)
        val authViewModel = AuthViewModel(database.userDao(), userPreferences)

        setContent {
            DACS3Theme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsState()
                val authEvent by authViewModel.authEvent.collectAsState()

                Scaffold(
                    bottomBar = {
                        if (authState is AuthState.Success) {
                            BottomBar(navController = navController)
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                navController = navController,
                                onLogin = { email, password ->
                                    authViewModel.login(email, password)
                                    connectToStringee(authViewModel.accessToken.toString())
                                },
                                authViewModel = authViewModel
                            )
                        }

                        composable(Screen.SignUp.route) {
                            SignUpScreen(
                                navController = navController,
                                onSignUp = { username, email, password ->
                                    authViewModel.signUp(username, email, password)
                                }
                            )
                        }

                        composable(BottomBarScreen.Profile.route) {
                            ProfileScreen(authViewModel = authViewModel)
                        }

                        composable(BottomBarScreen.Chat.route) {
                          //  ChatScreen()
                        }
                        composable(BottomBarScreen.Social.route) {
                            // SocialScreen()
                        }
                        composable(BottomBarScreen.VideoCall.route) {
                            VideoCallScreen(navController = navController)
                        }
                        composable(BottomBarScreen.Notification.route) {
                            // NotificationScreen()
                        }
                    }

                    LaunchedEffect(authState) {
                        when (authState) {
                            is AuthState.Success -> {
                                navController.navigate(BottomBarScreen.Profile.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                            else -> {}
                        }
                    }

                    LaunchedEffect(authEvent) {
                        when (authEvent) {
                            is AuthEvent.NavigateToLogin -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                                authViewModel.clearEvents()
                            }
                            null -> {}
                            is AuthEvent.ShowError -> TODO()
                        }
                    }
                }
            }
        }
    }
    private fun connectToStringee(accessToken: String) {
        stringeeClient = StringeeClient(this)
        stringeeClient.setConnectionListener((object : StringeeConnectionListener {
            override fun onConnectionConnected(client: StringeeClient, isReconnecting: Boolean) {
                Log.d("Stringee", "✅ Kết nối thành công với Stringee Server")
            }
            override fun onConnectionDisconnected(client: StringeeClient, isReconnecting: Boolean) {
                Log.d("Stringee", "⚠️ Đã ngắt kết nối khỏi Stringee Server")
            }

            override fun onIncomingCall(call: StringeeCall?) {
                Log.d("Stringee", "📞 Có cuộc gọi đến!")
            }

            override fun onIncomingCall2(call2: StringeeCall2?) {}
            override fun onConnectionError(p0: StringeeClient?, p1: StringeeError?) {
                TODO("Not yet implemented")
            }

//            override fun onConnectionError(client: StringeeClient, error: StringeeError) {
//                Log.e("Stringee", "❌ Lỗi kết nối: ${error.message}")
//            }

            override fun onRequestNewToken(client: StringeeClient) {
                Log.d("Stringee", "🔄 Yêu cầu cập nhật token")
            }

            override fun onCustomMessage(from: String?, msg: JSONObject?) {
                Log.d("Stringee", "📩 Tin nhắn từ $from: $msg")
            }

            override fun onTopicMessage(p0: String?, p1: JSONObject?) {
                TODO("Not yet implemented")
            }
        }))
        stringeeClient.connect(accessToken)
    }
}
