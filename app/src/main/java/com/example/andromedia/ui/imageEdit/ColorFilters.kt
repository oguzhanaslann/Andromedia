package com.example.andromedia.ui.imageEdit

import androidx.compose.ui.graphics.ColorMatrix
import com.oguzhanaslann.cropView.util.blue
import com.oguzhanaslann.cropView.util.gold
import com.oguzhanaslann.cropView.util.gray
import com.oguzhanaslann.cropView.util.green
import com.oguzhanaslann.cropView.util.none
import com.oguzhanaslann.cropView.util.pink
import com.oguzhanaslann.cropView.util.sepia
import com.oguzhanaslann.cropView.util.yellow

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