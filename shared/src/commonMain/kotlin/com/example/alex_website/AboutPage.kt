package com.example.alex_website

import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.ic_check
import alex_website.shared.generated.resources.ic_copy
import alex_website.shared.generated.resources.ic_location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun AboutPage() {
    val colors = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    Avatar(160.dp)
    Spacer(Modifier.height(24.dp))
    Text(Profile.NAME, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        // "Software Engineer, Kotlin @ JetBrains"
        Text("${Profile.ROLE}, ", fontSize = 17.sp, color = colors.onBackground)
        LinkText(Profile.TEAM, Profile.TEAM_URL)
        Text(" @ ", fontSize = 17.sp, color = colors.onSurfaceVariant)
        LinkText(Profile.COMPANY, Profile.COMPANY_URL)
    }
    Spacer(Modifier.height(4.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(Res.drawable.ic_location), contentDescription = null, Modifier.size(16.dp), tint = colors.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(Profile.LOCATION, fontSize = 15.sp, color = colors.onSurfaceVariant)
    }
    Spacer(Modifier.height(24.dp))
    MarkdownBody(Profile.ABOUT)
    Spacer(Modifier.height(28.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Profile.links.forEach { link ->
            // Icon and its copyable text are grouped so they wrap together.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(colors.surface)
                        .clickable { uriHandler.openUri(link.url) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(link.icon), contentDescription = link.label, Modifier.size(22.dp), tint = colors.onSurface)
                }
                if (link.copyText != null) {
                    Spacer(Modifier.width(8.dp))
                    CopyableContact(link)
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CopyableContact(link: SocialLink) {
    val colors = MaterialTheme.colorScheme
    val text = link.copyText ?: return
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) { delay(1500); copied = false }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text, fontSize = 15.sp, color = colors.onBackground)
        Spacer(Modifier.width(4.dp))
        Box(
            Modifier.size(32.dp).clip(CircleShape).clickable { copyToClipboard(text); copied = true },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(if (copied) Res.drawable.ic_check else Res.drawable.ic_copy),
                contentDescription = if (copied) "Copied" else "Copy ${link.label}",
                Modifier.size(16.dp),
                tint = if (copied) colors.primary else colors.onSurfaceVariant,
            )
        }
    }
}
