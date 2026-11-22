package com.example.alex_website

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownDimens
import com.mikepenz.markdown.compose.LocalMarkdownPadding
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.compose.elements.MarkdownCodeBackground
import com.mikepenz.markdown.compose.elements.MarkdownCodeFence

/** Code fence that highlights the languages we know and renders the rest as plain code. */
internal val highlightedCodeFence: MarkdownComponent = { model ->
    MarkdownCodeFence(model.content, model.node, style = model.typography.code) { code, language, style ->
        CodeBlock(code, language, style)
    }
}

@Composable
private fun CodeBlock(code: String, language: String?, style: TextStyle) {
    val palette = codePalette()
    val text = remember(code, language, palette) {
        when (language?.trim()?.lowercase()) {
            "wasm", "wat", "webassembly" -> highlightWat(code, palette)
            "kotlin", "kt", "kts" -> highlightKotlin(code, palette.kotlin)
            else -> AnnotatedString(code)
        }
    }
    MarkdownCodeBackground(
        color = LocalMarkdownColors.current.codeBackground,
        shape = RoundedCornerShape(LocalMarkdownDimens.current.codeBackgroundCornerSize),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(LocalMarkdownPadding.current.codeBlock),
        )
    }
}

internal data class CodePalette(
    /** Stack-switching instructions — the point of the posts, so they stand out most. */
    val stackSwitching: SpanStyle,
    val keyword: SpanStyle,
    val comment: SpanStyle,
    val kotlin: KotlinPalette,
)

@Composable
private fun codePalette(): CodePalette {
    val colors = MaterialTheme.colorScheme
    val dark = colors.background.luminance() < 0.5f
    return CodePalette(
        // Same orange as the "to handler" arrow in the diagrams.
        stackSwitching = SpanStyle(color = if (dark) Color(0xFFE8925C) else Color(0xFFC0612B), fontWeight = FontWeight.Bold),
        keyword = SpanStyle(color = colors.primary),
        comment = SpanStyle(color = colors.onSurfaceVariant, fontStyle = FontStyle.Italic),
        kotlin = if (dark) KotlinPalette.Dark else KotlinPalette.Light,
    )
}

/** Instructions and type constructors from the stack-switching proposal. */
private val STACK_SWITCHING = setOf(
    "cont", "cont.new", "cont.bind", "resume", "resume_throw", "suspend", "switch", "on", "tag",
)

/** Minimal highlighter for the WebAssembly text format. */
internal fun highlightWat(code: String, palette: CodePalette): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < code.length) {
        val c = code[i]
        when {
            code.startsWith(";;", i) -> {
                val end = code.indexOf('\n', i).let { if (it < 0) code.length else it }
                withStyle(palette.comment) { append(code, i, end) }
                i = end
            }
            code.startsWith("(;", i) -> {
                val end = code.indexOf(";)", i + 2).let { if (it < 0) code.length else it + 2 }
                withStyle(palette.comment) { append(code, i, end) }
                i = end
            }
            c == '$' || c.isLetter() -> {
                var end = i + 1
                while (end < code.length && (code[end].isLetterOrDigit() || code[end] in "._$'!-")) end++
                val word = code.substring(i, end)
                when {
                    word.startsWith('$') -> append(word)
                    word in STACK_SWITCHING -> withStyle(palette.stackSwitching) { append(word) }
                    else -> withStyle(palette.keyword) { append(word) }
                }
                i = end
            }
            else -> { append(c); i++ }
        }
    }
}
