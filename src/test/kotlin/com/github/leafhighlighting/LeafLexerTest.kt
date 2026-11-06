package com.github.leafhighlighting

import com.intellij.lexer.HtmlLexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType
import com.intellij.psi.xml.XmlTokenType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LeafLexerTest {
    @Test
    fun `highlights nested calls and directive body boundaries`() {
        val actual = tokenize("#if(count(users) > 0): #(user.name) #endif")

        assertEquals(
            listOf(
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TAG_NAME to "if",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.VARIABLE to "count",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.VARIABLE to "users",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.WHITESPACE to " ",
                LeafTokenTypes.OPERATOR to ">",
                LeafTokenTypes.WHITESPACE to " ",
                LeafTokenTypes.NUMBER to "0",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.COLON to ":",
                LeafTokenTypes.TEXT to " ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.VARIABLE to "user",
                LeafTokenTypes.DOT to ".",
                LeafTokenTypes.VARIABLE to "name",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.TEXT to " ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TAG_NAME to "endif",
            ),
            actual,
        )
    }

    @Test
    fun `keeps strings and unrecognized hashes as template content`() {
        val actual = tokenize("Hello #count(\"users\")! ## text")

        assertEquals(
            listOf(
                LeafTokenTypes.TEXT to "Hello ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TAG_NAME to "count",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.STRING to "\"users\"",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.TEXT to "! ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TEXT to "# text",
            ),
            actual,
        )
    }

    @Test
    fun `highlights custom tags with optional parameters and bodies`() {
        val actual = tokenize("The time is #now() #hello(\"John\") #panel(title): #(title) #endpanel")

        assertEquals(
            listOf(
                LeafTokenTypes.TEXT to "The time is ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TAG_NAME to "now",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.TEXT to " ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TAG_NAME to "hello",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.STRING to "\"John\"",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.TEXT to " ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TAG_NAME to "panel",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.VARIABLE to "title",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.COLON to ":",
                LeafTokenTypes.TEXT to " ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.LPAREN to "(",
                LeafTokenTypes.VARIABLE to "title",
                LeafTokenTypes.RPAREN to ")",
                LeafTokenTypes.TEXT to " ",
                LeafTokenTypes.HASH to "#",
                LeafTokenTypes.TAG_NAME to "endpanel",
            ),
            actual,
        )
    }

    @Test
    fun `layers HTML highlighting around Leaf expressions`() {
        val htmlHighlighter = TestHtmlHighlighter()
        val highlighter = LeafSyntaxHighlighter(htmlHighlighter)
        val text = "<p class=\"greeting\">Hello #(name)</p>"
        val lexer = highlighter.highlightingLexer
        lexer.start(text)
        val tokens = mutableListOf<Pair<IElementType, String>>()
        while (lexer.tokenType != null) {
            val type = lexer.tokenType!!
            tokens += type to text.substring(lexer.tokenStart, lexer.tokenEnd)
            lexer.advance()
        }

        assertTrue(tokens.contains(XmlTokenType.XML_NAME to "p"))
        assertTrue(tokens.contains(XmlTokenType.XML_NAME to "class"))
        assertTrue(tokens.contains(LeafTokenTypes.HASH to "#"))
        assertTrue(tokens.contains(LeafTokenTypes.VARIABLE to "name"))
        assertEquals(
            listOf(TestHtmlHighlighter.TAG_NAME),
            highlighter.getTokenHighlights(XmlTokenType.XML_NAME).toList(),
        )
    }

    @Test
    fun `highlights Leaf interpolations in text and quoted HTML attributes`() {
        val text = "#(var) ##(var) <span id=\"#(fieldID)-hint\"><a href=\"##(fieldID)\">#(fieldError)</a></span> " +
            "<input class=\"form-field__control\" id=\"#(fieldID)\" name=\"#(fieldID)\" type=\"text\" " +
            "value=\"#(fieldValue)\" aria-invalid=\"true\" aria-describedby=\"#(fieldID)-hint #(fieldID)-error\">"
        val lexer = LeafSyntaxHighlighter(TestHtmlHighlighter()).highlightingLexer
        lexer.start(text)
        val variables = mutableListOf<String>()
        while (lexer.tokenType != null) {
            if (lexer.tokenType == LeafTokenTypes.VARIABLE) {
                variables += text.substring(lexer.tokenStart, lexer.tokenEnd)
            }
            lexer.advance()
        }

        assertEquals(
            listOf("var", "var", "fieldID", "fieldID", "fieldError", "fieldID", "fieldID", "fieldValue", "fieldID", "fieldID"),
            variables,
        )
    }

    private class TestHtmlHighlighter : SyntaxHighlighter {
        override fun getHighlightingLexer() = HtmlLexer()

        override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
            if (tokenType == XmlTokenType.XML_NAME) arrayOf(TAG_NAME) else emptyArray()

        companion object {
            val TAG_NAME = TextAttributesKey.createTextAttributesKey("TEST_HTML_TAG_NAME")
        }
    }

    private fun tokenize(text: String): List<Pair<IElementType, String>> {
        val lexer = LeafLexer()
        lexer.start(text)
        val tokens = mutableListOf<Pair<IElementType, String>>()
        while (lexer.tokenType != null) {
            val type = lexer.tokenType!!
            tokens += type to text.substring(lexer.tokenStart, lexer.tokenEnd)
            lexer.advance()
        }
        return tokens
    }
}