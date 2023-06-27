package com.example.andromedia.ui.imageEdit

import com.example.andromedia.ui.blue
import com.example.andromedia.ui.gold
import com.example.andromedia.ui.gray
import com.example.andromedia.ui.green
import com.example.andromedia.ui.none
import com.example.andromedia.ui.pink
import com.example.andromedia.ui.sepia
import com.example.andromedia.ui.yellow

val grayFilter = ColorFilterModel(
    name = "Gray",
    colorMatrix = gray
)

val yellowFilter = ColorFilterModel(
    name = "Yellow",
    colorMatrix = yellow
)

val blueFilter = ColorFilterModel(
    name = "Blue",
    colorMatrix = blue
)

val goldFilter = ColorFilterModel(
    name = "Gold",
    colorMatrix = gold
)

val pinkFilter = ColorFilterModel(
    name = "Pink",
    colorMatrix = pink
)

val greenFilter = ColorFilterModel(
    name = "Green",
    colorMatrix = green
)

val sepiaFilter = ColorFilterModel(
    name = "Sepia",
    colorMatrix = sepia
)

val noneFilter = ColorFilterModel(
    name = "None",
    colorMatrix = none
)