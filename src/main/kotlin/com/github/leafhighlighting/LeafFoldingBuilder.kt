package com.github.leafhighlighting

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange

class LeafFoldingBuilder : FoldingBuilder {
    override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> {
        val text = node.text
        val baseOffset = node.textRange.startOffset
        return findFoldRanges(text)
            .map { range -> FoldingDescriptor(node, TextRange(baseOffset + range.startOffset, baseOffset + range.endOffset)) }
            .toTypedArray()
    }

    internal fun findFoldRanges(text: CharSequence): List<TextRange> {
        val stack = mutableListOf<Pair<String, Int>>()
        val folds = mutableListOf<TextRange>()
        val lexer = LeafLexer()
        lexer.start(text)
        val tokens = mutableListOf<Triple<String, Int, Int>>()
        var hashOffset: Int? = null
        while (lexer.tokenType != null) {
            val tagStart = hashOffset
            if (lexer.tokenType == LeafTokenTypes.TAG_NAME && tagStart != null) {
                tokens += Triple(text.substring(lexer.tokenStart, lexer.tokenEnd), tagStart, lexer.tokenEnd)
                hashOffset = null
            } else if (lexer.tokenType == LeafTokenTypes.HASH) {
                hashOffset = lexer.tokenStart
            } else if (lexer.tokenType != LeafTokenTypes.WHITESPACE) {
                hashOffset = null
            }
            lexer.advance()
        }
        tokens.forEach { (name, offset, tagEnd) ->
            if (name.isEmpty()) return@forEach
            when {
                name.startsWith("end") && name.length > 3 -> {
                    val expected = name.removePrefix("end")
                    val openIndex = stack.indexOfLast { it.first == expected }
                    if (openIndex >= 0) {
                        val (_, start) = stack.removeAt(openIndex)
                        val end = tagEnd
                        if (end - start > 1 && text.substring(start, end).contains('\n')) {
                            folds += TextRange(start, end)
                        }
                    }
                }
                name in BLOCK_TAGS -> stack += name to offset
            }
        }
        return folds
    }

    override fun getPlaceholderText(node: ASTNode): String = "…"

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    companion object {
        private val BLOCK_TAGS = setOf("if", "for", "while", "unless", "macro", "block", "section")
    }
}