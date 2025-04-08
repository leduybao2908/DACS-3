package com.example.dacs3.ui.screens.videocall

import VideoCallViewModel
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
//import com.example.dacs3.ui.screens.VideoCall.VideoCallViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.webrtc.SurfaceViewRenderer
import org.webrtc.EglBase

@Composable
fun VideoCallScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: VideoCallViewModel = viewModel()
    val callState = remember { mutableStateOf("Đang kết nối...") }

    val eglBase = remember { EglBase.create() }
    val remoteView = remember { SurfaceViewRenderer(context) }
    val localView = remember { SurfaceViewRenderer(context) }

    LaunchedEffect(Unit) {
        viewModel.initVideoCall(remoteView, localView) { state ->
            callState.value = state
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA7E8F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = callState.value, fontSize = 20.sp, color = Color.Black)

        // Hiển thị Video Call
        AndroidView(
            factory = {
                LinearLayout(it).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800)
                    addView(remoteView)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút bắt đầu cuộc gọi
        Button(
            onClick = { viewModel.startCall() },
            modifier = Modifier.size(80.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = "Start Call",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        // Nút kết thúc cuộc gọi
        Button(
            onClick = { viewModel.endCall() },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Kết Thúc Cuộc Gọi", color = Color.White)
        }
    }
}
