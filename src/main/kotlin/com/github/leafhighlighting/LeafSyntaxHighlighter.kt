package com.github.leafhighlighting

import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.lexer.LayeredLexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType

class LeafSyntaxHighlighter(
    private val htmlHighlighter: SyntaxHighlighter = HtmlFileHighlighter(),
) : SyntaxHighlighter {
    override fun getHighlightingLexer() = LayeredLexer(LeafLexer()).apply {
        registerLayer(htmlHighlighter.highlightingLexer, LeafTokenTypes.TEXT)
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        when (tokenType) {
            LeafTokenTypes.HASH -> HASH_KEYS
            LeafTokenTypes.TAG_NAME -> TAG_NAME_KEYS
            LeafTokenTypes.VARIABLE -> VARIABLE_KEYS
            LeafTokenTypes.KEYWORD -> KEYWORD_KEYS
            LeafTokenTypes.STRING -> STRING_KEYS
            LeafTokenTypes.NUMBER -> NUMBER_KEYS
            LeafTokenTypes.OPERATOR -> OPERATOR_KEYS
            LeafTokenTypes.LPAREN, LeafTokenTypes.RPAREN -> PARENTHESIS_KEYS
            LeafTokenTypes.DOT -> DOT_KEYS
            LeafTokenTypes.COMMA -> COMMA_KEYS
            LeafTokenTypes.COLON -> COLON_KEYS
            LeafTokenTypes.TEXT -> EMPTY_KEYS
            else -> htmlHighlighter.getTokenHighlights(tokenType)
        }

    companion object {
        val HASH = TextAttributesKey.createTextAttributesKey("LEAF_HASH", DefaultLanguageHighlighterColors.MARKUP_TAG)
        val TAG_NAME = TextAttributesKey.createTextAttributesKey("LEAF_TAG_NAME", DefaultLanguageHighlighterColors.KEYWORD)
        val VARIABLE = TextAttributesKey.createTextAttributesKey("LEAF_VARIABLE", DefaultLanguageHighlighterColors.IDENTIFIER)
        val KEYWORD = TextAttributesKey.createTextAttributesKey("LEAF_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val STRING = TextAttributesKey.createTextAttributesKey("LEAF_STRING", DefaultLanguageHighlighterColors.STRING)
        val NUMBER = TextAttributesKey.createTextAttributesKey("LEAF_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val OPERATOR = TextAttributesKey.createTextAttributesKey("LEAF_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val PARENTHESIS = TextAttributesKey.createTextAttributesKey("LEAF_PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES)
        val DOT = TextAttributesKey.createTextAttributesKey("LEAF_DOT", DefaultLanguageHighlighterColors.DOT)
        val COMMA = TextAttributesKey.createTextAttributesKey("LEAF_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val COLON = TextAttributesKey.createTextAttributesKey("LEAF_COLON", DefaultLanguageHighlighterColors.COMMA)

        private val HASH_KEYS = arrayOf(HASH)
        private val TAG_NAME_KEYS = arrayOf(TAG_NAME)
        private val VARIABLE_KEYS = arrayOf(VARIABLE)
        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val STRING_KEYS = arrayOf(STRING)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val OPERATOR_KEYS = arrayOf(OPERATOR)
        private val PARENTHESIS_KEYS = arrayOf(PARENTHESIS)
        private val DOT_KEYS = arrayOf(DOT)
        private val COMMA_KEYS = arrayOf(COMMA)
        private val COLON_KEYS = arrayOf(COLON)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}