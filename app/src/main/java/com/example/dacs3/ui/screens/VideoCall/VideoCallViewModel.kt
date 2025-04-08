import android.util.Log
import androidx.lifecycle.ViewModel
import org.webrtc.SurfaceViewRenderer

class VideoCallViewModel : ViewModel() {
    fun initVideoCall(remoteView: SurfaceViewRenderer, localView: SurfaceViewRenderer, onStateChange: (String) -> Unit) {
        Log.d("VideoCall", "Video call initialized")
        onStateChange("Cuộc gọi đã sẵn sàng")
    }

    fun startCall() {
        Log.d("VideoCall", "Starting video call")
    }

    fun endCall() {
        Log.d("VideoCall", "Ending video call")
    }
}