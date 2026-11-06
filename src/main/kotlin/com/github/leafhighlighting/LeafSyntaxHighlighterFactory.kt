package com.github.leafhighlighting

import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class LeafSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter =
        LeafSyntaxHighlighter(
            SyntaxHighlighterFactory.getSyntaxHighlighter(HtmlFileType.INSTANCE, project, virtualFile)
                ?: HtmlFileHighlighter(),
        )
}