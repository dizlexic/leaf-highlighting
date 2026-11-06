package com.github.leafhighlighting

import com.intellij.openapi.util.TextRange
import kotlin.test.Test
import kotlin.test.assertEquals

class LeafTagAnnotatorTest {
    private val annotator = LeafTagAnnotator()

    @Test
    fun `accepts correctly paired block tags`() {
        assertEquals(emptyList(), annotator.findTagDiagnostics("#if(condition): text #endif"))
    }

    @Test
    fun `accepts if branches inside a block`() {
        val text = "#if(first): content #else: fallback #endif\n#if(second): success #endif"

        assertEquals(emptyList(), annotator.findTagDiagnostics(text))
    }

    @Test
    fun `reports unexpected closing tags`() {
        val text = "content #endif"

        assertEquals(
            listOf(diagnostic(text, "endif", "Unexpected closing tag #endif")),
            annotator.findTagDiagnostics(text),
        )
    }

    @Test
    fun `reports unclosed block tags`() {
        val text = "#if(condition): content"

        assertEquals(
            listOf(diagnostic(text, "if", "Tag #if is not closed")),
            annotator.findTagDiagnostics(text),
        )
    }

    @Test
    fun `reports improperly nested block tags`() {
        val text = "#if(condition): #for(item in items): content #endif"

        assertEquals(
            listOf(diagnostic(text, "for", "Tag #for is not closed before #endif")),
            annotator.findTagDiagnostics(text),
        )
    }

    @Test
    fun `pairs custom tags with body separators`() {
        assertEquals(emptyList(), annotator.findTagDiagnostics("#panel(title): content #endpanel"))
    }

    private fun diagnostic(text: String, tag: String, message: String) =
        LeafTagAnnotator.TagDiagnostic(message, TextRange(text.indexOf(tag), text.indexOf(tag) + tag.length))
}