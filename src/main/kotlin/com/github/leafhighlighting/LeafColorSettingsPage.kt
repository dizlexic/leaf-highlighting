package com.github.leafhighlighting

import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class LeafColorSettingsPage : ColorSettingsPage {
    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = LeafSyntaxHighlighter(HtmlFileHighlighter())

    override fun getDemoText(): String =
        """#<TAG>if</TAG>(<VARIABLE>title</VARIABLE> == <STRING>"Welcome"</STRING>):
    <div class="welcome">Hello, #(<VARIABLE>title</VARIABLE>)!</div>
#<TAG>else</TAG>: Welcome!
#<TAG>endif</TAG>"""

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> =
        mapOf(
            "TAG" to LeafSyntaxHighlighter.TAG_NAME,
            "VARIABLE" to LeafSyntaxHighlighter.VARIABLE,
            "STRING" to LeafSyntaxHighlighter.STRING,
        )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> =
        arrayOf(
            AttributesDescriptor("Tag marker", LeafSyntaxHighlighter.HASH),
            AttributesDescriptor("Tag name", LeafSyntaxHighlighter.TAG_NAME),
            AttributesDescriptor("Expression variable", LeafSyntaxHighlighter.VARIABLE),
            AttributesDescriptor("Expression keyword", LeafSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("String", LeafSyntaxHighlighter.STRING),
            AttributesDescriptor("Number", LeafSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Operator", LeafSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Parentheses", LeafSyntaxHighlighter.PARENTHESIS),
            AttributesDescriptor("Dot", LeafSyntaxHighlighter.DOT),
            AttributesDescriptor("Comma", LeafSyntaxHighlighter.COMMA),
            AttributesDescriptor("Colon", LeafSyntaxHighlighter.COLON),
        )

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "Leaf"
}