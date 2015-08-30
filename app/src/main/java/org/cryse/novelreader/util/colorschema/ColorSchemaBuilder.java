package org.cryse.novelreader.util.colorschema;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.IntRange;

import com.amulyakhare.textdrawable.TextDrawable;

import org.cryse.novelreader.R;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class ColorSchemaBuilder {
    public static ColorSchema darkMode(Context context) {
        int textColor = ColorUtils.getColor(context.getResources(), R.color.text_color_night_read);
        int bgColor = ColorUtils.getColor(context.getResources(), R.color.theme_read_bg_color_white);
        float textSize = UIUtils.sp2px(context, 14f);
        return new ColorSchema(
                textColor,
                new ColorDrawable(bgColor),
                displayDrawableFromColor(textColor, textSize, bgColor)
        );
    }

    public static List<Drawable> displayDrawables(Context context, float textSize) {
        List<Drawable> colorSchemas = new ArrayList<>(10);
        for (int i = 0; i < 8; i++) {
            colorSchemas.add(displayDrawableByIndex(context, textSize, i));
        }
        return colorSchemas;
    }

    public static List<ColorSchema> buildColorSchemas(Context context) {
        List<ColorSchema> colorSchemas = new ArrayList<>(10);
        for (int i = 0; i < 8; i++) {
            colorSchemas.add(fromColorByIndex(context, i));
        }
        return colorSchemas;
    }

    public static ColorSchema defaultSchema(Context context) {
        return fromColorByIndex(context, 0);
    }

    public static ColorSchema fromIndex(Context context, int index) {
        if (index >= 0 && index < 8)
            return fromColorByIndex(context, index);
        else
            return defaultSchema(context);
    }

    private static ColorSchema fromColorByIndex(Context context, @IntRange(from = 0, to = 7) int index) {
        int textColor = ColorUtils.getColor(context.getResources(), R.color.text_color_primary);
        int bgColor = getColorFromColorsArray(context, R.array.read_bg_colors, index);
        return fromColor(context, textColor, bgColor);
    }

    public static Drawable displayDrawableByIndex(Context context, float textSize, int index) {
        if (index >= 0 && index < 8)
            return displayDrawableFromColorByIndex(context, textSize, index);
        else
            return displayDrawableFromColorByIndex(context, textSize, 0);
    }

    private static Drawable displayDrawableFromColorByIndex(Context context, float textSize, @IntRange(from = 0, to = 7) int index) {
        int textColor = ColorUtils.getColor(context.getResources(), R.color.text_color_primary);
        int bgColor = getColorFromColorsArray(context, R.array.read_bg_colors, index);
        return displayDrawableFromColor(textColor, textSize, bgColor);
    }

    private static ColorSchema fromColor(Context context, int textColor, int bgColor) {
        float textSize = UIUtils.sp2px(context, 14f);
        return new ColorSchema(
                textColor,
                new ColorDrawable(bgColor),
                displayDrawableFromColor(textColor, textSize, bgColor)
        );
    }

    private static Drawable displayDrawableFromColor(int textColor, float textSize, int bgColor) {
        return TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .fontSize((int) textSize)
                .withBorder(4)
                .endConfig()
                .buildRect("abc.xyz", bgColor);
    }

    private static int getColorFromColorsArray(Context context, @ArrayRes int colorsResId, int index) {
        final TypedArray ta = context.getResources().obtainTypedArray(colorsResId);
        int color = ta.getColor(index, Color.WHITE);
        ta.recycle();
        return color;
    }
}
