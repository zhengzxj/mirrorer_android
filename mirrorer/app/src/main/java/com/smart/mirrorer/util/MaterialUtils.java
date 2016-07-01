package com.smart.mirrorer.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by lzm on 16/4/6.
 */
public class MaterialUtils {
    /**
     * 创建tab条目的文字颜色
     *
     * @param normalColor
     * @param selectColor
     * @return
     */
    public static ColorStateList createTabItemTextColor(int normalColor, int selectColor) {
        return createColorStateList(new int[]{android.R.attr.state_selected, android.R.attr.state_checked,
                android.R.attr.state_pressed, -1}, new int[]{selectColor, selectColor, selectColor, normalColor});
    }

    /**
     * 对TextView设置不同状态时其文字颜色。
     */
    public static ColorStateList createColorStateList(int[] states, int[] colors) {
        int[] tempColors = colors;
        int[][] tempStates = new int[colors.length][];

        for (int i = 0; i < tempColors.length; i++) {
            if (states[i] == -1) {
                tempStates[i] = new int[]{android.R.attr.state_enabled};
            } else {
                tempStates[i] = new int[]{android.R.attr.state_enabled, states[i]};
            }
        }
        ColorStateList colorList = new ColorStateList(tempStates, tempColors);
        return colorList;
    }

    /**
     * 带选中效果空心圆角标签背景-引导页
     */
    public static Drawable createGuideCanSelectSolidStrokeBg(int normalColor, int selectedColor) {
        int cornersRadius = DeviceConfiger.dp2px(2);
        int strokeWidth = DeviceConfiger.dp2px(1);
        // 默认
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(Color.parseColor("#f7f7f7"));
        normalDrawable.setCornerRadius(cornersRadius);
        normalDrawable.setStroke(strokeWidth, normalColor);
        // 选中
        GradientDrawable selectDrawable = new GradientDrawable();
        selectDrawable.setColor(Color.parseColor("#e93838"));
        selectDrawable.setCornerRadius(cornersRadius);
        selectDrawable.setStroke(strokeWidth, selectedColor);

        Drawable itemDrawable = addStateDrawable(//
                new int[]{android.R.attr.state_selected, android.R.attr.state_checked, android.R.attr.state_pressed,
                        -1}, //
                new Drawable[]{selectDrawable, selectDrawable, selectDrawable, normalDrawable});
        return itemDrawable;
    }

    /**
     * 带选中效果空心圆角标签背景-引导页
     */
    public static Drawable createEvaluateCanSelectSolidStrokeBg(int normalColor, int selectedColor) {
        int cornersRadius = DeviceConfiger.dp2px(20);
        int strokeWidth = DeviceConfiger.dp2px(1);
        // 默认
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(Color.parseColor("#ffffff"));
        normalDrawable.setCornerRadius(cornersRadius);
        normalDrawable.setStroke(strokeWidth, normalColor);
        // 选中
        GradientDrawable selectDrawable = new GradientDrawable();
        selectDrawable.setColor(Color.parseColor("#ffffff"));
        selectDrawable.setCornerRadius(cornersRadius);
        selectDrawable.setStroke(strokeWidth, selectedColor);

        Drawable itemDrawable = addStateDrawable(//
                new int[]{android.R.attr.state_selected, android.R.attr.state_checked, android.R.attr.state_pressed,
                        -1}, //
                new Drawable[]{selectDrawable, selectDrawable, selectDrawable, normalDrawable});
        return itemDrawable;
    }

    /**
     * 创建StateListDrawablw对象。
     *
     * @param states
     * @param drawables
     * @return StateListDrawable 返回类型
     */
    public static StateListDrawable addStateDrawable(int[] states, Drawable[] drawables) {
        StateListDrawable sd = new StateListDrawable();
        Drawable normalDrawable = null;
        // 注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        // 所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        for (int i = 0; i < states.length; i++) {
            if (states[i] != -1) {
                sd.addState(new int[]{android.R.attr.state_enabled, states[i]}, drawables[i]);
                sd.addState(new int[]{states[i]}, drawables[i]);
            } else {
                normalDrawable = drawables[i];
            }
        }
        sd.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);
        sd.addState(new int[]{}, normalDrawable);
        return sd;
    }

    // 语音录制时使用的话筒背景
    public static Drawable createSolidStrokeBg(int solidColor, int bodyColor, int strokeWidth, int radius) {
        int cornersRadius = DeviceConfiger.dp2px(radius);
        // 默认
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(bodyColor);
        normalDrawable.setCornerRadius(cornersRadius);
        normalDrawable.setStroke(strokeWidth, solidColor);
        // 选中
        GradientDrawable selectDrawable = new GradientDrawable();
        selectDrawable.setColor(bodyColor);
        selectDrawable.setCornerRadius(cornersRadius);
        Drawable itemDrawable = addStateDrawable(//
                new int[]{android.R.attr.state_selected, android.R.attr.state_checked, android.R.attr.state_pressed,
                        -1}, //
                new Drawable[]{selectDrawable, selectDrawable, selectDrawable, normalDrawable});
        return itemDrawable;
    }

}
