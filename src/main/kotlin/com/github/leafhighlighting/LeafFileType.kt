package com.github.leafhighlighting

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class LeafFileType private constructor() : LanguageFileType(LeafLanguage) {
    override fun getName(): String = "Leaf"

    override fun getDescription(): String = "Leaf template"

    override fun getDefaultExtension(): String = "leaf"

    override fun getIcon(): Icon = IconLoader.getIcon("/icons/leaf.svg", LeafFileType::class.java)

    companion object {
        @JvmField
        val INSTANCE = LeafFileType()
    }
}