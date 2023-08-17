package com.race.game.domain.race

import com.race.game.core.library.XY
import kotlin.random.Random

data class RaceItem(
    val id: Int = Random.nextInt(),
    var isPresent: Boolean = false,
    var number: Int?,
    var isSelected: Boolean,
    val speed: Int = when (number) {
        in 1..4 -> 5
        in 5..8 -> 8
        in 9..13 -> 10
        else -> 12
    },
    override var y: Float = 0f,
    override var x: Float = 0f
) : XY