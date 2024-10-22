package com.example.livetvapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class ChannelViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ChannelUiState>(ChannelUiState.Loading)
    val uiState: StateFlow<ChannelUiState> = _uiState

    init {
        fetchChannels()
    }

    private fun fetchChannels() {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://dslive.site/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(ChannelService::class.java)
                val channelList = service.getChannels()
                _uiState.value = ChannelUiState.Success(channelList)
            } catch (e: Exception) {
                _uiState.value = ChannelUiState.Error("Failed to load channels: ${e.message}")
            }
        }
    }
}

interface ChannelService {
    @GET("channels.json")
    suspend fun getChannels(): List<Channel>
}

sealed class ChannelUiState {
    object Loading : ChannelUiState()
    data class Success(val channels: List<Channel>) : ChannelUiState()
    data class Error(val message: String) : ChannelUiState()
}