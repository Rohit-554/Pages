package io.jadu.pages.presentation.home_widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.paging.LOG_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class ThemeChangeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_CONFIGURATION_CHANGED) {
            Log.d("changeduitheme", "Theme changed")
            context?.let {
                CoroutineScope(Dispatchers.Default).launch {
                    TodoWidget.update(it)
                }
            }
        }
    }
}