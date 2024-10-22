package com.example.livetvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
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
import com.google.android.exoplayer2.util.Util

class PlayerActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val channel = intent.getSerializableExtra("channel") as Channel
        
        player = ExoPlayer.Builder(this).build()
        
        setContent {
            AndroidView(
                factory = { context ->
                    StyledPlayerView(context).apply {
                        this.player = this@PlayerActivity.player
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        setupPlayer(channel)
    }
    
    private fun setupPlayer(channel: Channel) {
        val drmCallback = HttpMediaDrmCallback(channel.key, DefaultHttpDataSource.Factory())
        
        val drmSessionManager = DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .build(drmCallback)
        
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        
        val dashMediaSource = DashMediaSource.Factory(dataSourceFactory)
            .setDrmSessionManager(drmSessionManager)
            .createMediaSource(MediaItem.fromUri(channel.playbackUrl))
        
        player.setMediaSource(dashMediaSource)
        player.prepare()
        player.play()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}