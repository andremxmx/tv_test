package com.example.livetvapp

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.C

@Composable
fun PlayerScreen(channelId: String) {
    val context = LocalContext.current
    val channel = remember(channelId) {
        // In a real app, you would fetch the channel details based on the channelId
        // For this example, we'll create a dummy channel
        Channel(
            id = channelId,
            name = "Sample Channel",
            logo = "",
            categorias = "",
            key = "sample_key",
            playbackUrl = "https://example.com/sample.mpd",
            clearKeyJson = ClearKeyJson(listOf(), "temporary")
        )
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(exoPlayer) {
        val drmCallback = HttpMediaDrmCallback(channel.key, DefaultHttpDataSource.Factory())
        
        val drmSessionManager = DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .build(drmCallback)
        
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        
        val dashMediaSource = DashMediaSource.Factory(dataSourceFactory)
            .setDrmSessionManager(drmSessionManager)
            .createMediaSource(MediaItem.fromUri(channel.playbackUrl))
        
        exoPlayer.setMediaSource(dashMediaSource)
        exoPlayer.prepare()
        exoPlayer.play()

        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { context ->
            StyledPlayerView(context).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}