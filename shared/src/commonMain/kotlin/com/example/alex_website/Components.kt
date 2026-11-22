package com.example.alex_website

import alex_website.shared.generated.resources.Res
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.MarkdownElement
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import org.intellij.markdown.MarkdownTokenTypes
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

/** Site-styled Markdown body text (About page, blog posts). */
@Composable
internal fun MarkdownBody(content: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val bodyText = TextStyle(fontSize = 16.sp, lineHeight = 26.sp, color = colors.onBackground)
    val heading = TextStyle(fontWeight = FontWeight.Bold, color = colors.onBackground)
    Markdown(
        content,
        modifier = modifier.fillMaxWidth(),
        typography = markdownTypography(
            paragraph = bodyText,
            text = bodyText,
            h1 = heading.copy(fontSize = 26.sp, lineHeight = 34.sp),
            h2 = heading.copy(fontSize = 22.sp, lineHeight = 30.sp),
            h3 = heading.copy(fontSize = 18.sp, lineHeight = 26.sp),
            code = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 22.sp, color = colors.onBackground),
            textLink = TextLinkStyles(
                // Always underlined: the accent alone is too close to the body text color (≈1.5:1 in dark theme).
                style = SpanStyle(color = colors.primary, fontWeight = FontWeight.SemiBold),
                hoveredStyle = SpanStyle(background = colors.primary.copy(alpha = 0.15f)),
            ),
        ),
        components = markdownComponents(codeFence = highlightedCodeFence),
        // The default layout puts a spacer before every node, including the EOL tokens between
        // paragraphs; instead, lay out only the real blocks, exactly one blank line apart.
        success = { state, components, modifier ->
            Column(modifier, verticalArrangement = Arrangement.spacedBy(26.dp)) {
                state.node.children
                    .filter { it.type != MarkdownTokenTypes.EOL }
                    .forEach { node ->
                        // A paragraph that is just `![caption](images/x.png)` becomes a full-width figure.
                        val figure = standaloneImage(node, state.content)
                        if (figure != null) PostImage(figure.path, figure.caption)
                        else MarkdownElement(node, components, state.content, includeSpacer = false)
                    }
            }
        },
    )
}

private class Figure(val path: String, val caption: String)

private val STANDALONE_IMAGE = Regex("""^!\[([^\]]*)]\(\s*([^)\s]+)\s*\)$""")

private fun standaloneImage(node: ASTNode, content: String): Figure? {
    if (node.type != MarkdownElementTypes.PARAGRAPH) return null
    val match = STANDALONE_IMAGE.matchEntire(node.getTextInNode(content).trim()) ?: return null
    return Figure(path = match.groupValues[2], caption = match.groupValues[1])
}

/**
 * An image from `blog/` (e.g. `images/diagram.png`), on a white card so it reads in both themes,
 * with the alt text as caption.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
private fun PostImage(path: String, caption: String) {
    val colors = MaterialTheme.colorScheme
    val bitmap by produceState<Result<ImageBitmap>?>(null, path) {
        value = runCatching { Res.readBytes("files/blog/$path").decodeToImageBitmap() }
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        when (val result = bitmap) {
            null -> Spacer(Modifier.height(200.dp))  // loading
            else -> result.fold(
                onSuccess = { image ->
                    Image(
                        image,
                        contentDescription = caption.ifBlank { null },
                        modifier = Modifier.fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.White)
                            .border(1.dp, colors.outlineVariant, MaterialTheme.shapes.medium)
                            .padding(12.dp),
                        contentScale = ContentScale.FillWidth,
                    )
                },
                onFailure = {
                    Text("Image not found: blog/$path", fontSize = 14.sp, color = colors.error)
                },
            )
        }
        if (caption.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(caption, fontSize = 14.sp, lineHeight = 20.sp, color = colors.onSurfaceVariant)
        }
    }
}
