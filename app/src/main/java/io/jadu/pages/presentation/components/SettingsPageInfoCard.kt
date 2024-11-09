package io.jadu.pages.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoCard(title: String, subtitle: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = subtitle,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}