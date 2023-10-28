package com.example.htmltext

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import com.example.htmltext.ui.theme.HTMLTextTheme


/** A consistent style for phone numbers and URLs in Text() blocks. */
@Composable
fun linkSpanStyle() = SpanStyle(Color.Blue)

/**
 * Converts a [Spanned] to an [AnnotatedString].
 * This method recognizes some simple html markup like <b> and <font color="...">, but excludes paragraph-level markup like <ul>.
 * Links are styled using [linkSpanStyle].
 */
@Composable
fun Spanned.toAnnotatedString(): AnnotatedString {
    val unformattedString = toString()
    return buildAnnotatedString {
        var listItems = 1
        append(unformattedString)

        getSpans<Any>()
            .forEach { span ->
                val start = getSpanStart(span)
                val end = getSpanEnd(span)

                when (span) {
                    is ForegroundColorSpan -> SpanStyle(color = Color(span.foregroundColor))
                    is RelativeSizeSpan -> SpanStyle(fontSize = LocalTextStyle.current.fontSize.times(span.sizeChange))
                    is StrikethroughSpan -> SpanStyle(textDecoration = TextDecoration.LineThrough)
                    is StyleSpan -> span.spanStyle()
                    is SuperscriptSpan -> SpanStyle(baselineShift = BaselineShift.Superscript)
                    is SubscriptSpan -> SpanStyle(baselineShift = BaselineShift.Subscript)
                    is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)
                    is URLSpan -> linkSpanStyle().also {
                        addStringAnnotation(
                            tag = URLSpan::javaClass.name,
                            annotation = span.url,
                            start = start,
                            end = end
                        )
                    }
                    is BulletSpan-> SpanStyle(fontSize = 20.sp)
                    else -> {
                        null
                    }
                }?.let { spanStyle ->
                    addStyle(spanStyle, start, end)
                }
            }
    }
}

private fun StyleSpan.spanStyle() = when(style) {
    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
    Typeface.BOLD_ITALIC -> SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
    else -> null
}

@Composable
fun DisplayHTMLText(@StringRes id: Int): AnnotatedString {
    return HtmlCompat.fromHtml(stringResource(id), HtmlCompat.FROM_HTML_MODE_COMPACT).toAnnotatedString()
}


@Preview(showBackground = true)
@Composable
fun HtmlTextPreview() {
    HTMLTextTheme {
        val html = """
            <p> <a href=".">This</a> is a link</p>
            <ul>
                <li>item 1</li>
                <li>item 2</li>
            </ul>
            <p> <b>bold</b>, <strong>strong</strong>, <em>emphasis</em>, <i>italic</i>, <u>underline</u>, <strike>strikethrough</strike> </p>
            <p> <sub>12</sub>Mg<sup>2+</sup> ⬅️ Magnesium </p>
            <p> <h6>Some</h6> <h4>title</h4> <h2>styles</h2> </p>
            <p> <font color="teal">Teal</font> <span style="color:#800080">Purple</span> </p>
            """

        Column {
            Text(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toAnnotatedString())

//            DisplayHTMLText(R.string.html_string)
        }
    }
}