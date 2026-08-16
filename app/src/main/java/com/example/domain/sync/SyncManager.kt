package com.example.domain.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.data.local.dao.ChatDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Synced(val itemsCount: Int, val timestamp: Long) : SyncState()
    data class Offline(val pendingCount: Int) : SyncState()
}

class SyncManager(
    private val context: Context,
    private val chatDao: ChatDao,
    private val scope: CoroutineScope
) {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    init {
        registerNetworkCallback()
        checkInitialConnectivity()
    }

    private fun checkInitialConnectivity() {
        val cm = connectivityManager ?: return
        val activeNetwork = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(activeNetwork)
        val online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        _isOnline.value = online
        if (online) {
            triggerAutoSync()
        }
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
                triggerAutoSync()
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
                scope.launch(Dispatchers.IO) {
                    val pending = chatDao.getPendingSyncMessages().size
                    _syncState.value = SyncState.Offline(pending)
                }
            }
        })
    }

    fun triggerAutoSync() {
        scope.launch(Dispatchers.IO) {
            try {
                _syncState.value = SyncState.Syncing
                val pendingMessages = chatDao.getPendingSyncMessages()
                if (pendingMessages.isNotEmpty()) {
                    // Simulate cryptographic handshake & sync with private cloud vault
                    kotlinx.coroutines.delay(800)
                    chatDao.markAllSynced()
                    _syncState.value = SyncState.Synced(pendingMessages.size, System.currentTimeMillis())
                } else {
                    _syncState.value = SyncState.Synced(0, System.currentTimeMillis())
                }
            } catch (e: Exception) {
                _syncState.value = SyncState.Idle
            }
        }
    }
}
