package com.pierbezuhoff.define.ui

import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

fun buildCustomTabsIntent(
    initialHeightPx: Int,
    lightColorScheme: Boolean,
    @ColorInt toolbarColor: Int,
) =
    CustomTabsIntent.Builder()
        .setColorScheme(
            if (lightColorScheme)
                CustomTabsIntent.COLOR_SCHEME_LIGHT
            else
                CustomTabsIntent.COLOR_SCHEME_DARK
        )
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(toolbarColor)
                .build()
        )
        .setInitialActivityHeightPx(
//            (Resources.getSystem().displayMetrics.heightPixels * 0.7f).roundToInt(),
            initialHeightPx,
            CustomTabsIntent.ACTIVITY_HEIGHT_ADJUSTABLE
        )
        .setUrlBarHidingEnabled(true)
        .setShowTitle(true)
        .build()