package com.example.alex_website

import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.ic_external
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun ResearchPage() {
    val colors = MaterialTheme.colorScheme
    Text("Research", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
    Spacer(Modifier.height(8.dp))
    Text("Publications and preprints", fontSize = 16.sp, color = colors.onSurfaceVariant)
    Spacer(Modifier.height(32.dp))
    Research.papers.forEachIndexed { i, paper ->
        if (i > 0) {
            HorizontalDivider(Modifier.padding(vertical = 32.dp), color = colors.outlineVariant)
        }
        PaperEntry(paper)
    }
}

@Composable
private fun PaperEntry(paper: Paper) {
    val colors = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    Column(Modifier.fillMaxWidth()) {
        paper.preview?.let { preview ->
            // Previews are figures cut from the papers (dark text on transparent), so they always
            // sit on a white card to stay legible in the dark theme.
            Box(
                Modifier.fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.White)
                    .border(1.dp, colors.outlineVariant, MaterialTheme.shapes.medium)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable { uriHandler.openUri(paper.url) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painterResource(preview),
                    contentDescription = "Figure from \"${paper.title}\"",
                    modifier = Modifier.heightIn(max = 220.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        LinkText(paper.title, paper.url, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            buildAnnotatedString {
                paper.authors.forEachIndexed { i, author ->
                    if (i > 0) append(", ")
                    if (author == Profile.NAME) {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.onBackground)) { append(author) }
                    } else {
                        append(author)
                    }
                }
            },
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(2.dp))
        Text(paper.venue, fontSize = 15.sp, fontStyle = FontStyle.Italic, color = colors.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Text(paper.summary, fontSize = 16.sp, lineHeight = 26.sp, color = colors.onBackground)
        if (paper.pdfUrl != null && paper.pdfUrl != paper.url) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinkText("PDF", paper.pdfUrl, fontSize = 15.sp)
                Spacer(Modifier.width(3.dp))
                Icon(
                    painterResource(Res.drawable.ic_external),
                    contentDescription = null,
                    Modifier.size(12.dp),
                    tint = colors.primary,
                )
            }
        }
    }
}
