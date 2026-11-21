package com.example.alex_website

import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.ic_external
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun Header(current: Page, dark: Boolean, onToggleTheme: () -> Unit, onNavigate: (Page) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val narrow = maxWidth < 640.dp
        val identity = @Composable {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.clip(MaterialTheme.shapes.small).clickable { onNavigate(Page.About) }.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(36.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        Profile.NAME,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(Modifier.width(8.dp))
                ThemeToggle(dark, onToggleTheme)
            }
        }
        val nav = @Composable {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Page.entries.forEach { NavItem(it, it == current) { onNavigate(it) } }
            }
        }
        if (narrow) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                identity()
                Spacer(Modifier.height(8.dp))
                nav()
            }
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                identity()
                nav()
            }
        }
    }
}

@Composable
private fun NavItem(page: Page, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier.clip(MaterialTheme.shapes.small).clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                page.title,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) colors.primary else colors.onSurfaceVariant,
            )
            if (page == Page.Cv) {
                Spacer(Modifier.width(2.dp))
                Icon(
                    painterResource(Res.drawable.ic_external),
                    contentDescription = "opens PDF",
                    Modifier.size(12.dp),
                    tint = colors.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier.height(2.dp).width(20.dp)
                .background(if (selected) colors.primary else Color.Transparent, CircleShape)
        )
    }
}

/** Sun in dark mode (click → light), moon in light mode (click → dark). */
@Composable
private fun ThemeToggle(dark: Boolean, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    val bg = MaterialTheme.colorScheme.background
    Box(
        Modifier.size(36.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(20.dp)) {
            val c = center
            val r = size.minDimension / 2
            if (dark) {
                drawCircle(color, radius = r * 0.42f, center = c)
                repeat(8) { i ->
                    val a = i * PI / 4
                    val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
                    drawLine(color, c + dir * (r * 0.62f), c + dir * (r * 0.95f), strokeWidth = r * 0.14f)
                }
            } else {
                drawCircle(color, radius = r * 0.8f, center = c)
                drawCircle(bg, radius = r * 0.66f, center = c + Offset(r * 0.38f, -r * 0.32f))
            }
        }
    }
}
