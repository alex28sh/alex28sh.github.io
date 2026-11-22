package com.example.alex_website

import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinHighlightTest {
    private val p = KotlinPalette.Light

    /** Every highlighted token of [code] mapped to the palette entry it got. */
    private fun tokens(code: String): List<Pair<String, String>> {
        val text = highlightKotlin(code, p)
        assertEquals(code, text.text, "highlighting must not change the text")
        val names = mapOf(
            p.keyword to "keyword", p.string to "string", p.number to "number", p.comment to "comment",
            p.annotation to "annotation", p.functionDeclaration to "fun", p.property to "property",
        )
        return text.spanStyles.map { text.text.substring(it.start, it.end) to names.getValue(it.item) }
    }

    @Test
    fun functionDeclarationWithReceiver() = assertEquals(
        listOf("suspend" to "keyword", "fun" to "keyword", "visit" to "fun", "null" to "keyword"),
        tokens("suspend fun SequenceScope<Int>.visit(node: Node?) = node == null"),
    )

    @Test
    fun propertyAccessButNotCalls() = assertEquals(
        listOf("left" to "property"),
        tokens("visit(node.left); node.toString()"),
    )

    @Test
    fun rangesAreNotFractions() = assertEquals(
        listOf("for" to "keyword", "in" to "keyword", "1" to "number", "10" to "number", "1.5" to "number"),
        tokens("for (i in 1..10) f(1.5)"),
    )

    @Test
    fun stringTemplates() = assertEquals(
        listOf("\"" to "string", "a " to "string", "$" to "keyword", "\${" to "keyword", "}" to "keyword", "\"" to "string"),
        tokens("\"a \$x\${y}\""),
    )

    @Test
    fun commentsAnnotationsAndLabels() = assertEquals(
        listOf("@OptIn" to "annotation", "class" to "keyword", "return" to "keyword", "@forEach" to "keyword", "// done" to "comment"),
        tokens("@OptIn(X::class) return@forEach // done"),
    )

    @Test
    fun contextKeywordsAsNames() = assertEquals(
        listOf("value" to "keyword", "class" to "keyword", "val" to "keyword", "val" to "keyword", "value" to "property"),
        tokens("value class V(val value: Int, val get: Int) = x.value"),
    )
}
