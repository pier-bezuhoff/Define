package com.pierbezuhoff.define.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.modifier.ModifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.model.RGB
import com.pierbezuhoff.define.R
import com.pierbezuhoff.define.ui.colorpicker.ClassicColorPicker
import com.pierbezuhoff.define.ui.colorpicker.HsvColor

internal val PREDEFINED_COLORS = listOf(
    Color.White, Color.LightGray, Color.Gray, Color.DarkGray, Color.Black,
    // RGB & CMY[-K] -> R Y G C B M
    Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta,
    // fun colors
    Color(0xFF_FFC0CB),
    Color(0xFF_FF7373),
    Color(0xFF_800000),
    Color(0xFF_321E1E),
    Color(0xFF_F08A5D),
    Color(0xFF_FFA500),
    Color(0xFF_FFD700),
    Color(0xFF_065535),
    Color(0xFF_08D9D6), // visually similar to cyan
    Color(0xFF_008080),
    Color(0xFF_6A2C70),
    Color(0xff_2ca3ff),
)

@Composable
fun ColorPickerDialog(
    currentColor: Color,
    onCancel: () -> Unit,
    onConfirm: (Color) -> Unit,
) {
    val colorState = rememberSaveable(currentColor, stateSaver = HsvColor.Saver) {
        mutableStateOf(HsvColor.from(currentColor))
    }
    val color = colorState.value.toColor()
    val setColor = remember(colorState) { { newColor: Color ->
        colorState.value = HsvColor.from(newColor)
    } }
    val lightDarkVerticalGradientBrush = remember { Brush.verticalGradient(
        0.1f to Color.White,
        0.9f to Color.Black,
    ) } // to grasp how the color looks in different contexts
    val lightDarkHorizontalGradientBrush = remember { Brush.horizontalGradient(
        0.1f to Color.White,
        0.9f to Color.Black,
    ) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact =
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT || windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
    val isMedium =
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM && windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.MEDIUM
    val isExpanded =
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED && windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.EXPANDED
    val isLandscape =
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM && windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
    val maxColorsPerRowLandscape = 11
    val maxColorsPerRowPortrait = if (isMedium) 8 else 6
    val paletteModifier = Modifier
        .padding(4.dp)
        .border(2.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
//        .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium)
    val swatchBgModifier = Modifier
        .padding(4.dp)
        .size(
            if (isCompact) 30.dp
            else if (isExpanded) 60.dp
            else 45.dp
        )
    val splashIconModifier = Modifier
        .size(
            if (isCompact) 24.dp
            else if (isExpanded) 40.dp
            else 32.dp
        )

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            if (isLandscape) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DialogTitle()
                    Row {
                        Column(horizontalAlignment = Alignment.End) {
                            ColorPickerDisplay(
                                colorState,
                                Modifier.fillMaxHeight(0.8f),
                            )
                            Row(
                                Modifier
                                    .requiredHeightIn(50.dp, 100.dp) // desperation constraint
                                ,
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                HexInput(
                                    color,
                                    setColor = { colorState.value = HsvColor.from(it) },
                                    onConfirm = { onConfirm(colorState.value.toColor()) }
                                )
                                OkButton(onClick = onCancel)
                                CancelButton {
                                    onConfirm(colorState.value.toColor())
                                }
                            }
                        }
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(top = 12.dp, end = 8.dp),
                        ) {
                            Box(
                                Modifier
                                    .padding(start = 4.dp, bottom = 8.dp) // outer offset
                                    .background(
                                        lightDarkVerticalGradientBrush,
                                        MaterialTheme.shapes.medium
                                    )
                                    .padding(12.dp)
                                    .padding(end = 40.dp) // adjust for 2nd circle-box offset
                            ) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(currentColor)
                                        .clickable { setColor(currentColor) }
                                ) {}
                                Box(
                                    Modifier
                                        .offset(x = 40.dp)
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable(enabled = false, onClick = {}) // blocks thru-clicks
                                ) {}
                            }
                            FlowRow(
                                paletteModifier,
                                maxItemsInEachRow = maxColorsPerRowLandscape,
                            ) {
                                for (clr in PREDEFINED_COLORS) {
                                    IconButton(
                                        onClick = { setColor(clr) },
                                        modifier = swatchBgModifier,
                                        colors = IconButtonDefaults.iconButtonColors().copy(
                                            contentColor = clr
                                        )
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.paint_splash),
                                            "Predefined color",
                                            splashIconModifier,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else { // portrait
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DialogTitle()
                    ColorPickerDisplay(
                        colorState,
                        Modifier.fillMaxWidth(
                            if (isMedium) 0.6f else 0.8f
                        ),
                    )
                    Row {
                        Box(
                            Modifier
                                .padding(top = 4.dp, start = 4.dp, end = 8.dp)
                                .background(
                                    lightDarkHorizontalGradientBrush,
                                    MaterialTheme.shapes.medium
                                )
                                .padding(12.dp)
                                .padding(bottom = 40.dp) // adjust for 2nd circle-box offset
                        ) {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(currentColor)
                                    .clickable { setColor(currentColor) }
                            ) {}
                            Box(
                                Modifier
                                    .offset(y = 40.dp)
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable(enabled = false, onClick = {}) // blocks thru-clicks
                            ) {}
                        }
                        Column {
                            FlowRow(
                                paletteModifier,
                                maxItemsInEachRow = maxColorsPerRowPortrait,
                            ) {
                                for (clr in PREDEFINED_COLORS) {
                                    IconButton(
                                        onClick = { setColor(clr) },
                                        modifier = swatchBgModifier,
                                        colors = IconButtonDefaults.iconButtonColors().copy(
                                            contentColor = clr
                                        )
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.paint_splash),
                                            "Predefined color",
                                            splashIconModifier,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HexInput(
                            color,
                            setColor = { colorState.value = HsvColor.from(it) },
                            onConfirm = { onConfirm(colorState.value.toColor()) },
                        )
                        OkButton(onClick = onCancel)
                        CancelButton {
                            onConfirm(colorState.value.toColor())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.DialogTitle() {
    Text(
        "Pick a color",
        Modifier
            .padding(16.dp)
            .align(Alignment.CenterHorizontally)
        ,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun OkButton(
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        shape = CircleShape,
    ) {
        Text("OK", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun CancelButton(
    onClick: () -> Unit,
) {
    val color = MaterialTheme.colorScheme.onSurface
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
        colors = ButtonDefaults.outlinedButtonColors().copy(
            contentColor = color
        ),
        border = BorderStroke(2.dp, color),
        shape = CircleShape,
    ) {
        Text("Cancel", style = MaterialTheme.typography.titleLarge)
    }
}

private fun computeHexTFV(color: Color): TextFieldValue {
    val hexString = RGB(color.red, color.green, color.blue)
        .toHex(withNumberSign = false, renderAlpha = RenderCondition.NEVER)
    return TextFieldValue(hexString, TextRange(hexString.length))
}

/**
 * @param[hsvColorState] this state is updated internally by [ClassicColorPicker]
 */
@Composable
private fun ColorPickerDisplay(
    hsvColorState: MutableState<HsvColor>,
    modifier: Modifier = Modifier,
    onColorChanged: () -> Unit = {},
) {
    ClassicColorPicker(
        modifier
            .aspectRatio(1.1f)
            .padding(16.dp)
        ,
        colorPickerValueState = hsvColorState,
        showAlphaBar = true,
        onColorChanged = { onColorChanged() }
    )
}

// MAYBE: append opacity to hex#
/**
 * @param[onConfirm] shortcut confirm exit lambda
 */
@Composable
private fun HexInput(
    color: Color,
    modifier: Modifier = Modifier,
    setColor: (Color) -> Unit,
    onConfirm: () -> Unit,
) {
    var hexTFV by remember(color) {
        mutableStateOf(computeHexTFV(color))
    }
    val windowInfo = LocalWindowInfo.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isError by remember(color) { mutableStateOf(false) }
    OutlinedTextField(
        value = hexTFV,
        onValueChange = { new ->
            hexTFV = new
            val hexString = new.text.let {
                if (it.isNotEmpty() && it[0] == '#')
                    it.drop(1) // drop leading '#'
                else it
            }
            if (hexString.length == 6) { // primitive hex validation
                try {
                    val rgb = RGB(hexString)
                    setColor(Color(rgb.r, rgb.g, rgb.b))
                    isError = false
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    println("cannot parse hex string \"$hexString\"")
                    isError = true
                }
            } else {
                isError = true
            }
        },
//                    textStyle = TextStyle(fontSize = 16.sp),
        label = { Text("hex #") },
        placeholder = { Text("RRGGBB", color = LocalContentColor.current.copy(alpha = 0.5f)) },
        isError = isError,
        keyboardOptions = KeyboardOptions( // smart ass enter capturing
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done,
            showKeyboardOnFocus = false, // this sadly does nothing...
        ),
        keyboardActions = KeyboardActions(
            onDone = { onConfirm() }
        ),
        singleLine = true,
        modifier = modifier
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    onConfirm()
                    true
                } else false
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            // overrides min width of 280.dp defined for TextField
            .widthIn(50.dp, 100.dp)
        ,
//        colors = OutlinedTextFieldDefaults.colors()
//            .copy(unfocusedContainerColor = color.value.toColor())
    )
    // NOTE: this (no focus by default on Android) fix only works 90% of time...
    // reference: https://stackoverflow.com/q/71412537/7143065
    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) { // runs once every time the dialog is opened
                focusRequester.freeFocus()
                focusManager.clearFocus()
                keyboard?.hide() // suppresses rare auto-showing keyboard bug
            }
        }
    }
}