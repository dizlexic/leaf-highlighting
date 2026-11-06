package com.github.leafhighlighting

import com.intellij.psi.tree.IElementType

internal object LeafTokenTypes {
    val TEXT = IElementType("LEAF_TEXT", LeafLanguage)
    val HASH = IElementType("LEAF_HASH", LeafLanguage)
    val TAG_NAME = IElementType("LEAF_TAG_NAME", LeafLanguage)
    val VARIABLE = IElementType("LEAF_VARIABLE", LeafLanguage)
    val KEYWORD = IElementType("LEAF_KEYWORD", LeafLanguage)
    val STRING = IElementType("LEAF_STRING", LeafLanguage)
    val NUMBER = IElementType("LEAF_NUMBER", LeafLanguage)
    val OPERATOR = IElementType("LEAF_OPERATOR", LeafLanguage)
    val LPAREN = IElementType("LEAF_LPAREN", LeafLanguage)
    val RPAREN = IElementType("LEAF_RPAREN", LeafLanguage)
    val DOT = IElementType("LEAF_DOT", LeafLanguage)
    val COMMA = IElementType("LEAF_COMMA", LeafLanguage)
    val COLON = IElementType("LEAF_COLON", LeafLanguage)
    val WHITESPACE = IElementType("LEAF_WHITESPACE", LeafLanguage)
    val OTHER = IElementType("LEAF_OTHER", LeafLanguage)
}