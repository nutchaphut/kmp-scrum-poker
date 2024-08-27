package com.nuzchpt.scrumpoker.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.roundToIntRect
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    radius: Float = 250f,
) {
    fun toRadians(degrees: Double): Double {
        return degrees * 3.14159265358979323846 / 180.0
    }
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        // Calculate angular spacing between individual items
        val angularSeparation = 360 / placeables.size

        // Represent the available working area by a rectangle
        val boundedRectangle = Rect(
            center = Offset(
                x = 0f,
                y = 0f,
            ),
            radius = radius + placeables.first().height,
        ).roundToIntRect()

        // Calculate the center of the working area and use it later for trig calculations
        val center = IntOffset(boundedRectangle.width / 2, boundedRectangle.height / 2)

        // Constrain our layout to the working area
        layout(boundedRectangle.width, boundedRectangle.height) {
            var requiredAngle = 0.0

            placeables.forEach { placeable ->
                // Calculate x,y coordinates where the layout will be placed on the
                // circumference of the circle using the required angle
                val x = center.x + (radius * sin(toRadians(requiredAngle))).toInt()
                val y = center.y + (radius * cos(toRadians(requiredAngle))).toInt()

                placeable.placeRelative(x - placeable.width / 2, y - placeable.height / 2)

                requiredAngle += angularSeparation
            }
        }
    }


}