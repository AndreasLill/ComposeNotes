package com.andlill.keynotes.ui.shared.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.andlill.keynotes.utils.StringUtils.indexesOfSubstring

enum class AnnotationType {
    First,
    All
}

@Composable
fun AnnotatedText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    annotation: String,
    annotationStyle: SpanStyle,
    annotationType: AnnotationType = AnnotationType.First
) {
    val annotatedText = remember(text, annotation) {
        buildAnnotatedString {
            append(text)
            when (annotationType) {
                AnnotationType.First -> {
                    text.indexesOfSubstring(annotation).firstOrNull()?.let {
                        addStyle(annotationStyle, it.first, it.second)
                    }
                }
                AnnotationType.All -> {
                    // Apply style to each annotation found in text.
                    text.indexesOfSubstring(annotation).forEach {
                        addStyle(annotationStyle, it.first, it.second)
                    }
                }
            }
        }
    }
    Text(
        modifier = modifier,
        text = annotatedText,
        style = textStyle
    )
}