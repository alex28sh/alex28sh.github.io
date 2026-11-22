package com.example.alex_website

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun BlogPage(onOpen: (BlogPost) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Text("Blog", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
    Spacer(Modifier.height(32.dp))
    if (blogPosts.isEmpty()) {
        Text("No posts yet.", fontSize = 16.sp, color = colors.onSurfaceVariant)
        return
    }
    blogPosts.forEachIndexed { i, post ->
        if (i > 0) HorizontalDivider(Modifier.padding(vertical = 8.dp), color = colors.outlineVariant)
        Column(
            Modifier.fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable { onOpen(post) }
                .padding(vertical = 16.dp, horizontal = 4.dp),
        ) {
            Text(formatDate(post.date), fontSize = 14.sp, color = colors.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(post.title, fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, color = colors.primary)
            if (post.summary.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(post.summary, fontSize = 16.sp, lineHeight = 24.sp, color = colors.onBackground)
            }
        }
    }
}

@Composable
internal fun BlogPostPage(slug: String, onBack: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val post = blogPosts.find { it.slug == slug } ?: return EmptyPage("Not found", "There is no post \"$slug\".")
    Text(
        "← All posts",
        fontSize = 15.sp,
        color = colors.primary,
        modifier = Modifier.clip(MaterialTheme.shapes.small).pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onBack).padding(vertical = 4.dp),
    )
    Spacer(Modifier.height(24.dp))
    Text(post.title, fontSize = 30.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
    Spacer(Modifier.height(8.dp))
    Text(formatDate(post.date), fontSize = 15.sp, color = colors.onSurfaceVariant)
    Spacer(Modifier.height(28.dp))
    MarkdownBody(post.body)
}
