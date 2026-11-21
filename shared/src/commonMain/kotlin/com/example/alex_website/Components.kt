package com.example.alex_website

import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.profile
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun Avatar(size: Dp) {
    Image(
        painterResource(Res.drawable.profile),
        contentDescription = Profile.NAME,
        modifier = Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
        contentScale = ContentScale.Crop,
    )
}

/** Accent-colored text link: hand cursor and underline on hover. */
@Composable
internal fun LinkText(
    text: String,
    url: String,
    fontSize: TextUnit = 17.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    val uriHandler = LocalUriHandler.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Text(
        text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        lineHeight = lineHeight,
        color = MaterialTheme.colorScheme.primary,
        textDecoration = if (hovered) TextDecoration.Underline else null,
        modifier = Modifier
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(interactionSource = interaction, indication = null) { uriHandler.openUri(url) },
    )
}

@Composable
internal fun EmptyPage(title: String, message: String) {
    val colors = MaterialTheme.colorScheme
    Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
    Spacer(Modifier.height(16.dp))
    Text(message, fontSize = 16.sp, color = colors.onSurfaceVariant)
}
