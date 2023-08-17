package com.race.game.core.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class GameViewModel : ViewModel() {
    var gameState = true
    var pauseState = false
    internal var gameScope = CoroutineScope(Dispatchers.Default)

    internal val _playerXY = MutableLiveData<XY>()
    val playerXY: LiveData<XY> = _playerXY

    internal suspend fun moveSomethingDown(
        maxY: Int,
        objHeight: Int,
        objWidth: Int,
        playerWidth: Int,
        playerHeight: Int,
        oldList: MutableList<XY>,
        onIntersect: ((XY) -> Unit) = {},
        onOutOfScreen: ((XY) -> Unit) = {},
        distance: Int
    ): MutableList<XY> {
        return suspendCoroutine { cn ->
            val newList = oldList.toMutableList()
            oldList.forEachIndexed { index, obj ->
                if (obj.y <= maxY) {
                    val objX = obj.x.toInt()..obj.x.toInt() + objWidth
                    val objY = obj.y.toInt()..obj.y.toInt() + objHeight
                    val playerX = _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                    val playerY = _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight
                    if (playerX.any { it in objX } && playerY.any { it in objY }) {
                        onIntersect.invoke(obj)
                        newList.remove(obj)
                    } else {
                        newList[index].y = newList[index].y + distance
                    }
                } else {
                    onOutOfScreen.invoke(obj)
                    newList.remove(obj)
                }
            }
            cn.resume(newList)
        }
    }

    fun stop() {
        gameScope.cancel()
    }
}