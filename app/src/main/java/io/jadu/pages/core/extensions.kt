package io.jadu.pages.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo

fun Modifier.noRippleClickable(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "noRippleClickable"
    }
) {
    this.clickable(
        indication = null,
        interactionSource = interactionSource,
        onClick = onClick
    )
}