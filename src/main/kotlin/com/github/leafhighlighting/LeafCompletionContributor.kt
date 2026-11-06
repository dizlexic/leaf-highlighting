package com.github.leafhighlighting

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class LeafCompletionContributor : com.intellij.codeInsight.completion.CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(LeafLanguage),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet,
                ) {
                    TAGS.forEach { (tag, priority) ->
                        result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(tag), priority))
                    }
                }
            },
        )
    }

    companion object {
        private val TAGS = listOf(
            "if" to 10.0,
            "else" to 9.0,
            "elseif" to 8.0,
            "endif" to 10.0,
            "for" to 9.0,
            "endfor" to 9.0,
            "while" to 8.0,
            "endwhile" to 8.0,
            "set" to 7.0,
            "include" to 7.0,
            "embed" to 7.0,
        )
    }
}