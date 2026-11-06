package com.github.leafhighlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class LeafTagAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.containingFile != element) return

        findTagDiagnostics(element.text).forEach { diagnostic ->
            holder.newAnnotation(HighlightSeverity.ERROR, diagnostic.message)
                .range(diagnostic.range)
                .create()
        }
    }

    internal fun findTagDiagnostics(text: String): List<TagDiagnostic> {
        val lexer = LeafLexer()
        lexer.start(text)
        val tokens = mutableListOf<Token>()
        while (lexer.tokenType != null) {
            tokens += Token(lexer.tokenType!!, lexer.tokenStart, lexer.tokenEnd, text.substring(lexer.tokenStart, lexer.tokenEnd))
            lexer.advance()
        }

        val tags = mutableListOf<Tag>()
        tokens.forEachIndexed { index, token ->
            if (token.type == LeafTokenTypes.TAG_NAME && index > 0 && tokens[index - 1].type == LeafTokenTypes.HASH) {
                val nextHash = (index + 1 until tokens.size).firstOrNull { tokens[it].type == LeafTokenTypes.HASH } ?: tokens.size
                val hasBody = (index + 1 until nextHash).any { tokens[it].type == LeafTokenTypes.COLON }
                tags += Tag(token.text, token.start, token.end, hasBody)
            }
        }

        val diagnostics = mutableListOf<TagDiagnostic>()
        val stack = mutableListOf<Tag>()
        tags.forEach { tag ->
            when {
                tag.name.startsWith("end") && tag.name.length > 3 -> {
                    val expected = tag.name.removePrefix("end")
                    val match = stack.indexOfLast { it.name == expected }
                    if (match < 0) {
                        diagnostics += diagnostic(tag, "Unexpected closing tag #${tag.name}")
                    } else {
                        for (index in stack.lastIndex downTo match + 1) {
                            diagnostics += diagnostic(stack[index], "Tag #${stack[index].name} is not closed before #${tag.name}")
                        }
                        stack.subList(match, stack.size).clear()
                    }
                }

                tag.name in BRANCH_TAGS -> Unit
                tag.name in BLOCK_TAGS || tag.hasBody -> stack += tag
            }
        }
        stack.forEach { diagnostics += diagnostic(it, "Tag #${it.name} is not closed") }
        return diagnostics
    }

    private fun diagnostic(tag: Tag, message: String) = TagDiagnostic(message, TextRange(tag.start, tag.end))

    private data class Tag(val name: String, val start: Int, val end: Int, val hasBody: Boolean)

    private data class Token(val type: com.intellij.psi.tree.IElementType, val start: Int, val end: Int, val text: String)

    internal data class TagDiagnostic(val message: String, val range: TextRange)

    private companion object {
        val BLOCK_TAGS = setOf("if", "for", "while", "unless", "macro", "block", "section")
        val BRANCH_TAGS = setOf("else", "elseif")
    }
}