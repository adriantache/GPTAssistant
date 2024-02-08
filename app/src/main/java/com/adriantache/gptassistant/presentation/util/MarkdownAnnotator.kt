package com.adriantache.gptassistant.presentation.util

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

// TODO: clean up and rewrite relevant parts of this file
@Composable
fun Markdown(
    modifier: Modifier = Modifier,
    text: String,
) {
    val markdown = MarkdownParser(CommonMarkFlavourDescriptor())
        .parse(MarkdownElementTypes.MARKDOWN_FILE, text, true)

    return Markdown(
        modifier = modifier,
        text = text,
        markdown = markdown
    )
}

// Adapted from https://github.com/takahirom/jetpack-compose-markdown

@Composable
private fun Markdown(
    modifier: Modifier = Modifier,
    text: String,
    markdown: ASTNode
) {
    val builder = AnnotatedString.Builder()
    val images = mutableListOf<Pair<Int, String>>()
    val links = mutableListOf<Pair<IntRange, String>>()

    showTree(markdown, 0, text)

    builder.appendMarkdown(
        markdownText = text,
        node = markdown,
        onInlineContents = { position, link ->
            images.add(position to link)
        },
        onLinkContents = { positionRange, url ->
            links.add(positionRange to url)
        }
    )

    val inlineContents = images.associate { (_, link) ->
        link to InlineTextContent(
            Placeholder(
                150.sp,
                150.sp,
                PlaceholderVerticalAlign.TextCenter
            )
        ) {
            AsyncImage(
                model = link,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Inside,
            )
        }

    }

    ClickableText(
        modifier = modifier,
        text = builder.toAnnotatedString(),
        inlineContent = inlineContents,
    ) { position ->
        Log.d("Markdown", "link clicked:" + links.firstOrNull { it.first.contains(position) })
    }
}

private fun showTree(
    node: ASTNode,
    depth: Int,
    text: String,
) {
    val treeStringBuilder = StringBuilder()

    treeStringBuilder.appendLine(
        " ".repeat(depth) +
                node.type +
                " " +
                node.getTextInNode(text).toString().take(10) +
                "..."
    )

    node.children.forEach {
        showTree(it, depth = depth + 1, text)
    }
}

@Composable
fun ClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Unit,
) {
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    // TODO: test and understand this
    val pressIndicator = Modifier.pointerInput(text) {
        detectTapGestures {
            layoutResult?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(it))
            }
        }
    }

    Text(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        inlineContent = inlineContent,
        onTextLayout = {
            layoutResult = it
            onTextLayout(it)
        }
    )
}

fun AnnotatedString.Builder.appendMarkdown(
    markdownText: String,
    node: ASTNode,
    depth: Int = 0,
    onInlineContents: (position: Int, link: String) -> Unit,
    onLinkContents: (positionRange: IntRange, url: String) -> Unit,
): AnnotatedString.Builder {
    when (node.type) {
        MarkdownElementTypes.MARKDOWN_FILE, MarkdownElementTypes.PARAGRAPH -> {
            node.children.forEach { childNode ->
                appendMarkdown(
                    markdownText = markdownText,
                    node = childNode,
                    depth = depth + 1,
                    onInlineContents = onInlineContents,
                    onLinkContents = onLinkContents
                )
            }
        }

        MarkdownElementTypes.SETEXT_1, MarkdownElementTypes.ATX_1 -> {
            withStyle(SpanStyle(fontSize = 24.sp)) {
                node.children.subList(1, node.children.size).forEach { childNode ->
                    appendMarkdown(
                        markdownText = markdownText,
                        node = childNode,
                        depth = depth + 1,
                        onInlineContents = onInlineContents,
                        onLinkContents = onLinkContents
                    )
                }
            }
        }

        MarkdownElementTypes.SETEXT_2, MarkdownElementTypes.ATX_2 -> {
            withStyle(SpanStyle(fontSize = 20.sp)) {
                node.children.subList(1, node.children.size).forEach { childNode ->
                    appendMarkdown(
                        markdownText = markdownText,
                        node = childNode,
                        depth = depth + 1,
                        onInlineContents = onInlineContents,
                        onLinkContents = onLinkContents
                    )
                }
            }
        }

        MarkdownElementTypes.CODE_SPAN -> {
            withStyle(SpanStyle(background = Color.LightGray)) {
                node.children.subList(1, node.children.size - 1)
                    .forEach { childNode ->
                        appendMarkdown(
                            markdownText = markdownText,
                            node = childNode,
                            depth = depth + 1,
                            onInlineContents = onInlineContents,
                            onLinkContents = onLinkContents
                        )
                    }
            }
        }

        MarkdownElementTypes.STRONG -> {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                node.children
                    .drop(2)
                    .dropLast(2)
                    .forEach { childNode ->
                        appendMarkdown(
                            markdownText = markdownText,
                            node = childNode,
                            depth = depth + 1,
                            onInlineContents = onInlineContents,
                            onLinkContents = onLinkContents
                        )
                    }
            }
        }

        MarkdownElementTypes.EMPH -> {
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                node.children
                    .drop(1)
                    .dropLast(1)
                    .forEach { childNode ->
                        appendMarkdown(
                            markdownText = markdownText,
                            node = childNode,
                            depth = depth + 1,
                            onInlineContents = onInlineContents,
                            onLinkContents = onLinkContents
                        )
                    }
            }
        }

        MarkdownElementTypes.CODE_FENCE -> {
            withStyle(SpanStyle(background = Color.Gray)) {
                node.children
                    .drop(1)
                    .dropLast(1)
                    .forEach { childNode ->
                        appendMarkdown(
                            markdownText = markdownText,
                            node = childNode,
                            depth = depth + 1,
                            onInlineContents = onInlineContents,
                            onLinkContents = onLinkContents
                        )
                    }
            }
        }

        MarkdownElementTypes.IMAGE -> {
            val linkNode = node.children[node.children.size - 1]
            if (linkNode.children.size > 2) {
                val link =
                    linkNode.children[linkNode.children.size - 2].getTextInNode(markdownText)
                onInlineContents(node.startOffset, link.toString())
                appendInlineContent(link.toString(), link.toString())
            }
        }

        MarkdownElementTypes.INLINE_LINK -> {
            val linkDestination = node.children.findLast { it.type == MarkdownElementTypes.LINK_DESTINATION }
                ?: return this
            val linkText = node.children.find { it.type == MarkdownElementTypes.LINK_TEXT }!!
                .children[1]
            if (linkDestination.children.size > 2) {
                val link =
                    linkDestination.getTextInNode(markdownText).toString()
                val start = this.length
                withStyle(SpanStyle(color = Color.Blue)) {
                    appendMarkdown(
                        markdownText = markdownText,
                        node = linkText,
                        depth = depth + 1,
                        onInlineContents = onInlineContents,
                        onLinkContents = onLinkContents
                    )
                    val end = this.length
                    onLinkContents(start..end, link)
                }
            }
        }

        else -> {
            append(
                text = node.getTextInNode(markdownText).toString()
            )
        }
    }

    return this
}

class MarkdownPreviewProvider : PreviewParameterProvider<String> {
    private val texts = listOf(
        "# test",
        "## aaaa",
        "abc**abc**",
        "*abc*",
        "_abc_",
        "```let abc = \"def\"```",
        "**ab*ab*ab**",
    )

    override val values: Sequence<String> = texts.asSequence()

    override val count: Int = texts.size
}

@Preview(showBackground = true, backgroundColor = 0xfff)
@Composable
fun DefaultPreview(@PreviewParameter(provider = MarkdownPreviewProvider::class) text: String) {
    Markdown(text = text)
}