package com.github.leafhighlighting

import com.intellij.openapi.util.TextRange
import kotlin.test.Test
import kotlin.test.assertEquals

class LeafFoldingBuilderTest {
    private val foldingBuilder = LeafFoldingBuilder()

    @Test
    fun `folds multiline directives from opening hash through closing tag`() {
        val text = "#if(condition):\n    content\n#endif"

        assertEquals(
            listOf(TextRange(text.indexOf("#if"), text.length)),
            foldingBuilder.findFoldRanges(text),
        )
    }

    @Test
    fun `does not fold single line directives`() {
        assertEquals(emptyList(), foldingBuilder.findFoldRanges("#if(condition): content #endif"))
    }
}