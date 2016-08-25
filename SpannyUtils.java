package com.pwc.talentexchange.common.utils;

import android.text.SpannableString;
import android.text.Spanned;

/**
 * Created by jzhang506 on 6/20/2016.
 */
public class SpannyUtils {
    /*
    * Span lists:
    *
    * 1.new TypefaceSpan() arg: "monospace", "serif", and "sans-serif".
    *
    * 2.new ForegroundColorSpan() arg: int color
    *
    * 3.new BackgroundColorSpan() arg: int color
    *
    * 4.new StyleSpan() arg: Typeface.NORMAL,Typeface.BOLD,Typeface.ITALIC,Typeface.BOLD_ITALIC
    *
    * 5.new UnderlineSpan()
    *
    * 6.new StrikethroughSpan()
    *
    * 7.new URLSpan() arg: String Url
    *
    * 8.new ClickableSpan()
    *
    * */

    public static SpannableString spanText(CharSequence text, Object... spans) {
        SpannableString spannableString = new SpannableString(text);
        for (Object span : spans) {
            spannableString.setSpan(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public static SpannableString spanText(CharSequence text, Object span) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
