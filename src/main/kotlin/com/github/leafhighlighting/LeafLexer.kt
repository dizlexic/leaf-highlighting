package com.github.leafhighlighting

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class LeafLexer : LexerBase() {
    private enum class Mode {
        TEXT,
        AFTER_HASH,
        AFTER_TAG,
        TAG_EXPRESSION,
        INTERPOLATION_EXPRESSION,
        AFTER_TAG_ARGUMENTS,
    }

    private lateinit var buffer: CharSequence
    private var bufferEnd = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null
    private var mode = Mode.TEXT
    private var nestingDepth = 0

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        bufferEnd = endOffset
        tokenStart = startOffset
        tokenEnd = startOffset
        mode = Mode.entries[initialState and STATE_MODE_MASK]
        nestingDepth = initialState ushr STATE_DEPTH_SHIFT
        advance()
    }

    override fun getState(): Int = mode.ordinal or (nestingDepth shl STATE_DEPTH_SHIFT)

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenType = null
        while (tokenEnd < bufferEnd && tokenType == null) {
            tokenStart = tokenEnd
            when (mode) {
                Mode.TEXT -> lexText()
                Mode.AFTER_HASH -> lexAfterHash()
                Mode.AFTER_TAG -> lexAfterTag()
                Mode.TAG_EXPRESSION -> lexExpression(isTag = true)
                Mode.INTERPOLATION_EXPRESSION -> lexExpression(isTag = false)
                Mode.AFTER_TAG_ARGUMENTS -> lexAfterTagArguments()
            }
        }
        if (tokenType == null) {
            tokenStart = bufferEnd
            tokenEnd = bufferEnd
        }
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

    private fun lexText() {
        if (buffer[tokenEnd] == '#') {
            tokenEnd++
            tokenType = LeafTokenTypes.HASH
            mode = Mode.AFTER_HASH
            return
        }

        while (tokenEnd < bufferEnd && buffer[tokenEnd] != '#') {
            tokenEnd++
        }
        tokenType = LeafTokenTypes.TEXT
    }

    private fun lexAfterHash() {
        val current = buffer[tokenEnd]
        if (current == '(') {
            tokenEnd++
            tokenType = LeafTokenTypes.LPAREN
            nestingDepth = 1
            mode = Mode.INTERPOLATION_EXPRESSION
        } else if (current.isIdentifierStart()) {
            tokenEnd = scanIdentifier(tokenEnd)
            tokenType = LeafTokenTypes.TAG_NAME
            mode = Mode.AFTER_TAG
        } else if (current == '#' && tokenEnd + 1 < bufferEnd && buffer[tokenEnd + 1] == '(') {
            tokenEnd++
            tokenType = LeafTokenTypes.HASH
        } else if (current == '#') {
            tokenEnd++
            while (tokenEnd < bufferEnd && buffer[tokenEnd] != '#') {
                tokenEnd++
            }
            tokenType = LeafTokenTypes.TEXT
            mode = Mode.TEXT
        } else {
            mode = Mode.TEXT
        }
    }

    private fun lexAfterTag() {
        when (buffer[tokenEnd]) {
            '(' -> {
                tokenEnd++
                tokenType = LeafTokenTypes.LPAREN
                nestingDepth = 1
                mode = Mode.TAG_EXPRESSION
            }

            ':' -> {
                tokenEnd++
                tokenType = LeafTokenTypes.COLON
                mode = Mode.TEXT
            }

            else -> mode = Mode.TEXT
        }
    }

    private fun lexAfterTagArguments() {
        if (buffer[tokenEnd] == ':') {
            tokenEnd++
            tokenType = LeafTokenTypes.COLON
        }
        mode = Mode.TEXT
    }

    private fun lexExpression(isTag: Boolean) {
        val current = buffer[tokenEnd]
        when {
            current.isWhitespace() -> lexWhitespace()
            current == '"' || current == '\'' -> lexString(current)
            current.isDigit() -> lexNumber()
            current.isIdentifierStart() -> lexExpressionIdentifier()
            current == '(' -> {
                tokenEnd++
                nestingDepth++
                tokenType = LeafTokenTypes.LPAREN
            }

            current == ')' -> {
                tokenEnd++
                nestingDepth--
                tokenType = LeafTokenTypes.RPAREN
                if (nestingDepth == 0) {
                    mode = if (isTag) Mode.AFTER_TAG_ARGUMENTS else Mode.TEXT
                }
            }

            current == '.' -> emitSingle(LeafTokenTypes.DOT)
            current == ',' -> emitSingle(LeafTokenTypes.COMMA)
            current == ':' -> emitSingle(LeafTokenTypes.COLON)
            current in OPERATOR_CHARACTERS -> lexOperator()
            else -> emitSingle(LeafTokenTypes.OTHER)
        }
    }

    private fun lexWhitespace() {
        while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
            tokenEnd++
        }
        tokenType = LeafTokenTypes.WHITESPACE
    }

    private fun lexString(quote: Char) {
        tokenEnd++
        while (tokenEnd < bufferEnd) {
            val current = buffer[tokenEnd++]
            if (current == '\\' && tokenEnd < bufferEnd) {
                tokenEnd++
            } else if (current == quote) {
                break
            }
        }
        tokenType = LeafTokenTypes.STRING
    }

    private fun lexNumber() {
        while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
            tokenEnd++
        }
        if (tokenEnd + 1 < bufferEnd && buffer[tokenEnd] == '.' && buffer[tokenEnd + 1].isDigit()) {
            tokenEnd++
            while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) {
                tokenEnd++
            }
        }
        tokenType = LeafTokenTypes.NUMBER
    }

    private fun lexExpressionIdentifier() {
        tokenEnd = scanIdentifier(tokenEnd)
        val value = buffer.subSequence(tokenStart, tokenEnd).toString()
        tokenType = if (value in EXPRESSION_KEYWORDS) LeafTokenTypes.KEYWORD else LeafTokenTypes.VARIABLE
    }

    private fun lexOperator() {
        while (tokenEnd < bufferEnd && buffer[tokenEnd] in OPERATOR_CHARACTERS) {
            tokenEnd++
        }
        tokenType = LeafTokenTypes.OPERATOR
    }

    private fun emitSingle(type: IElementType) {
        tokenEnd++
        tokenType = type
    }

    private fun scanIdentifier(start: Int): Int {
        var index = start + 1
        while (index < bufferEnd && buffer[index].isIdentifierPart()) {
            index++
        }
        return index
    }

    private fun Char.isIdentifierStart(): Boolean = isLetter() || this == '_'

    private fun Char.isIdentifierPart(): Boolean = isLetterOrDigit() || this == '_'

    private companion object {
        const val STATE_MODE_MASK = 0xFF
        const val STATE_DEPTH_SHIFT = 8
        const val OPERATOR_CHARACTERS = "+-*/%=!<>&|"
        val EXPRESSION_KEYWORDS = setOf("in", "true", "false", "nil")
    }
}