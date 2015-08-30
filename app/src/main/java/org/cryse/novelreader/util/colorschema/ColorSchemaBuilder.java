package org.cryse.novelreader.util.colorschema;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;

import com.amulyakhare.textdrawable.TextDrawable;

import org.cryse.novelreader.R;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class ColorSchemaBuilder {
    private Context mContext;
    private int mTextSize;
    private int mDisplayBorderSize;
    private String mDisplayText;

    private ColorSchemaBuilder(Context context) {
        mContext = context;
        mTextSize = (int) UIUtils.sp2px(mContext, 14f);
        mDisplayBorderSize = 4;
        mDisplayText = "abc";
    }

    public static ColorSchemaBuilder with(Context context) {
        return new ColorSchemaBuilder(context);
    }

    private static int getBgColorFromColorsArray(Context context, @ArrayRes int colorsResId, int index) {
        final TypedArray ta = context.getResources().obtainTypedArray(colorsResId);
        int color = ta.getColor(index, Color.WHITE);
        ta.recycle();
        return color;
    }

    private static int getTextColorFromColorsArray(Context context, @ArrayRes int colorsResId, int index) {
        final TypedArray ta = context.getResources().obtainTypedArray(colorsResId);
        int color = ta.getColor(index, Color.WHITE);
        ta.recycle();
        return color;
    }

    public ColorSchemaBuilder textSize(int textSize) {
        mTextSize = textSize;
        return this;
    }

    public ColorSchemaBuilder textSizeDimen(@DimenRes int textSizeDimen) {
        mTextSize = mContext.getResources().getDimensionPixelSize(textSizeDimen);
        return this;
    }

    public ColorSchemaBuilder borderSize(int textColor) {
        mDisplayBorderSize = textColor;
        return this;
    }

    public ColorSchemaBuilder borderSizeRes(@DimenRes int borderSizeDimen) {
        mDisplayBorderSize = mContext.getResources().getDimensionPixelSize(borderSizeDimen);
        return this;
    }

    public ColorSchemaBuilder text(String text) {
        mDisplayText = text;
        return this;
    }

    public ColorSchemaBuilder textRes(@StringRes int stringRes) {
        mDisplayText = mContext.getString(stringRes);
        return this;
    }

    public ColorSchema darkMode() {
        int bgColor = ColorUtils.getColor(mContext.getResources(), R.color.theme_read_bg_color_white);
        int textColor = ColorUtils.getColor(mContext.getResources(), R.color.text_color_night_read);
        return new ColorSchema(
                textColor,
                new ColorDrawable(bgColor),
                displayDrawableFromColor(textColor, bgColor)
        );
    }

    public List<Drawable> displayDrawables() {
        List<Drawable> colorSchemas = new ArrayList<>(10);
        for (int i = 0; i < 8; i++) {
            colorSchemas.add(displayDrawableByIndex(i));
        }
        return colorSchemas;
    }

    public List<ColorSchema> buildColorSchemas(Context context) {
        List<ColorSchema> colorSchemas = new ArrayList<>(10);
        for (int i = 0; i < 8; i++) {
            colorSchemas.add(fromColorByIndex(i));
        }
        return colorSchemas;
    }

    public ColorSchema defaultSchema() {
        return fromColorByIndex(0);
    }

    public ColorSchema byIndex(int index) {
        if (index >= 0 && index < 8)
            return fromColorByIndex(index);
        else
            return defaultSchema();
    }

    private ColorSchema fromColorByIndex(@IntRange(from = 0, to = 7) int index) {
        int bgColor = getBgColorFromColorsArray(mContext, R.array.read_bg_colors, index);
        int textColor = getTextColorFromColorsArray(mContext, R.array.read_text_colors, index);
        return fromColor(textColor, bgColor);
    }

    public Drawable displayDrawableByIndex(int index) {
        if (index >= 0 && index < 8)
            return displayDrawableFromColorByIndex(index);
        else
            return displayDrawableFromColorByIndex(0);
    }

    private Drawable displayDrawableFromColorByIndex(@IntRange(from = 0, to = 7) int index) {
        int bgColor = getBgColorFromColorsArray(mContext, R.array.read_bg_colors, index);
        int textColor = getTextColorFromColorsArray(mContext, R.array.read_text_colors, index);
        return displayDrawableFromColor(textColor, bgColor);
    }

    private ColorSchema fromColor(int textColor, int bgColor) {
        return new ColorSchema(
                textColor,
                new ColorDrawable(bgColor),
                displayDrawableFromColor(textColor, bgColor)
        );
    }

    private Drawable displayDrawableFromColor(int textColor, int bgColor) {
        return TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .fontSize(mTextSize)
                .withBorder(mDisplayBorderSize)
                .endConfig()
                .buildRect(mDisplayText, bgColor);
    }
}
