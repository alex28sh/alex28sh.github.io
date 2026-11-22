package com.example.alex_website

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle

/** IntelliJ-like colors: the "Light" scheme in the light theme, "Darcula" in the dark one. */
internal data class KotlinPalette(
    val keyword: SpanStyle,
    val string: SpanStyle,
    val number: SpanStyle,
    val comment: SpanStyle,
    val annotation: SpanStyle,
    val functionDeclaration: SpanStyle,
    val property: SpanStyle,
) {
    companion object {
        val Light = KotlinPalette(
            keyword = SpanStyle(color = Color(0xFF0033B3)),
            string = SpanStyle(color = Color(0xFF067D17)),
            number = SpanStyle(color = Color(0xFF1750EB)),
            comment = SpanStyle(color = Color(0xFF8C8C8C), fontStyle = FontStyle.Italic),
            annotation = SpanStyle(color = Color(0xFF9E880D)),
            functionDeclaration = SpanStyle(color = Color(0xFF00627A)),
            property = SpanStyle(color = Color(0xFF871094)),
        )
        val Dark = KotlinPalette(
            keyword = SpanStyle(color = Color(0xFFCC7832)),
            string = SpanStyle(color = Color(0xFF6A8759)),
            number = SpanStyle(color = Color(0xFF6897BB)),
            comment = SpanStyle(color = Color(0xFF808080), fontStyle = FontStyle.Italic),
            annotation = SpanStyle(color = Color(0xFFBBB529)),
            functionDeclaration = SpanStyle(color = Color(0xFFFFC66D)),
            property = SpanStyle(color = Color(0xFF9876AA)),
        )
    }
}

private val KOTLIN_KEYWORDS = setOf(
    // hard
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface", "is",
    "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias", "typeof",
    "val", "var", "when", "while",
    // soft keywords that are rarely used as names; context-only ones (`value`, `field`, `get`, `set`, `file`…)
    // are common identifiers, so they are left out — except `value class`, handled below
    "by", "catch", "constructor", "finally", "import", "init", "where",
    "abstract", "actual", "annotation", "companion", "const", "crossinline", "data", "enum", "expect",
    "external", "final", "infix", "inline", "inner", "internal", "lateinit", "noinline", "open", "operator",
    "out", "override", "private", "protected", "public", "reified", "sealed", "suspend", "tailrec", "vararg",
)

/**
 * Lexical Kotlin highlighter. It has no semantic information, so it approximates the IDE:
 * `name` after `.` (and not called) is treated as a property, the name before `(` after `fun`
 * as a function declaration.
 */
internal fun highlightKotlin(code: String, palette: KotlinPalette): AnnotatedString =
    buildAnnotatedString { appendKotlin(code, palette) }

private fun AnnotatedString.Builder.appendKotlin(code: String, p: KotlinPalette) {
    var i = 0
    var expectFunName = false

    fun nextNonSpace(from: Int): Char? {
        var j = from
        while (j < code.length && code[j] == ' ') j++
        return code.getOrNull(j)
    }

    fun prevNonSpace(before: Int): Int {
        var j = before - 1
        while (j >= 0 && code[j] == ' ') j--
        return j
    }

    while (i < code.length) {
        val c = code[i]
        when {
            code.startsWith("//", i) -> {
                val end = code.indexOf('\n', i).let { if (it < 0) code.length else it }
                withStyle(p.comment) { append(code, i, end) }
                i = end
            }
            code.startsWith("/*", i) -> {
                val end = code.indexOf("*/", i + 2).let { if (it < 0) code.length else it + 2 }
                withStyle(p.comment) { append(code, i, end) }
                i = end
            }
            code.startsWith("\"\"\"", i) -> i = appendString(code, i, raw = true, p)
            c == '"' -> i = appendString(code, i, raw = false, p)
            c == '\'' -> {
                var end = i + 1
                while (end < code.length && code[end] != '\'' && code[end] != '\n') end += if (code[end] == '\\') 2 else 1
                end = minOf(end + 1, code.length)
                withStyle(p.string) { append(code, i, end) }
                i = end
            }
            c == '@' && i + 1 < code.length && code[i + 1].isLetter() -> {
                var end = i + 1
                while (end < code.length && code[end].isLetterOrDigit()) end++
                // `return@label` / `this@Outer` are labels; `@Foo` at the start of a token is an annotation.
                val isLabel = i > 0 && code[i - 1].isLetterOrDigit()
                withStyle(if (isLabel) p.keyword else p.annotation) { append(code, i, end) }
                i = end
            }
            c.isDigit() -> {
                var end = i + 1
                while (end < code.length) {
                    val d = code[end]
                    val fractionDot = d == '.' && code.getOrNull(end + 1)?.isDigit() == true
                    if (d.isLetterOrDigit() || d == '_' || fractionDot) end++ else break
                }
                withStyle(p.number) { append(code, i, end) }
                i = end
            }
            c.isLetter() || c == '_' || c == '`' -> {
                var end = i + 1
                if (c == '`') {
                    end = code.indexOf('`', i + 1).let { if (it < 0) code.length else it + 1 }
                } else {
                    while (end < code.length && (code[end].isLetterOrDigit() || code[end] == '_')) end++
                }
                val word = code.substring(i, end)
                val next = nextNonSpace(end)
                val prev = prevNonSpace(i)
                val afterDot = prev >= 0 && code[prev] == '.' && (prev == 0 || code[prev - 1] != '.')
                when {
                    (word in KOTLIN_KEYWORDS || word == "value" && code.startsWith("class", end + 1)) && !afterDot -> {
                        withStyle(p.keyword) { append(word) }
                        if (word == "fun") expectFunName = true
                    }
                    expectFunName && next == '(' -> {
                        withStyle(p.functionDeclaration) { append(word) }
                        expectFunName = false
                    }
                    afterDot && next != '(' && next != '{' && word[0].isLowerCase() ->
                        withStyle(p.property) { append(word) }
                    else -> append(word)
                }
                i = end
            }
            else -> {
                if (c == '(' || c == '{' || c == '=') expectFunName = false
                append(c)
                i++
            }
        }
    }
}

/** Appends a string literal starting at [start] with `$name` / `${...}` templates; returns the end index. */
private fun AnnotatedString.Builder.appendString(code: String, start: Int, raw: Boolean, p: KotlinPalette): Int {
    val quote = if (raw) "\"\"\"" else "\""
    withStyle(p.string) { append(quote) }
    var i = start + quote.length
    while (i < code.length) {
        when {
            code.startsWith(quote, i) -> {
                withStyle(p.string) { append(quote) }
                return i + quote.length
            }
            !raw && code[i] == '\n' -> return i  // unterminated: stop at end of line
            !raw && code[i] == '\\' && i + 1 < code.length -> {
                withStyle(p.keyword) { append(code, i, i + 2) }
                i += 2
            }
            code.startsWith("\${", i) -> {
                var depth = 1
                var end = i + 2
                while (end < code.length && depth > 0) {
                    if (code[end] == '{') depth++ else if (code[end] == '}') depth--
                    end++
                }
                val innerEnd = if (depth == 0) end - 1 else end
                withStyle(p.keyword) { append("\${") }
                appendKotlin(code.substring(i + 2, innerEnd), p)
                if (depth == 0) withStyle(p.keyword) { append("}") }
                i = end
            }
            code[i] == '$' && code.getOrNull(i + 1)?.let { it.isLetter() || it == '_' } == true -> {
                var end = i + 1
                while (end < code.length && (code[end].isLetterOrDigit() || code[end] == '_')) end++
                withStyle(p.keyword) { append('$') }
                append(code, i + 1, end)
                i = end
            }
            else -> {
                // A run of plain string characters, up to the next quote, escape, template or newline.
                var end = i + 1
                while (end < code.length && code[end] != '$' && code[end] != '\\' && code[end] != '\n' &&
                    !code.startsWith(quote, end)
                ) end++
                withStyle(p.string) { append(code, i, end) }
                i = end
            }
        }
    }
    return i
}
