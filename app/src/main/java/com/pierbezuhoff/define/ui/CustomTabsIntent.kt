package com.pierbezuhoff.define.ui

import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

// idk it's always full-height for some reason
fun buildCustomTabsIntent(
    initialHeightPx: Int,
    lightColorScheme: Boolean,
    @ColorInt toolbarColor: Int,
): CustomTabsIntent =
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
            initialHeightPx,
            CustomTabsIntent.ACTIVITY_HEIGHT_ADJUSTABLE
        )
        .setUrlBarHidingEnabled(true)
        .setShowTitle(true)
        .build()