package com.race.game.ui.race

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.race.game.core.library.l
import com.race.game.core.library.random
import com.race.game.domain.race.RaceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RaceViewModel : ViewModel() {
    private var gameScope = CoroutineScope(Dispatchers.Default)

    private val _listInside = MutableLiveData<MutableList<RaceItem>>(mutableListOf())
    val listInside: LiveData<MutableList<RaceItem>> = _listInside

    private val _listOutside = MutableLiveData<MutableList<RaceItem>>(mutableListOf())
    val listOutside: LiveData<MutableList<RaceItem>> = _listOutside

    private val _placeImg = MutableLiveData(false to 1)
    val placeImg: LiveData<Pair<Boolean, Int>> = _placeImg

    private val _balance = MutableLiveData(200)
    val balance: LiveData<Int> = _balance

    private val _price = MutableLiveData(200)
    val price: LiveData<Int> = _price

    init {
        _listInside.postValue(
            mutableListOf(
                RaceItem(number = 1, isSelected = false),
                RaceItem(number = null, isSelected = false)
            )
        )
    }

    fun removeFromRace(item: RaceItem) {
        val currentListInside = _listInside.value!!.toMutableList()
        val currentListOutside = _listOutside.value!!.toMutableList()

        currentListOutside.removeAll { it.id == item.id }
        currentListInside.reverse()
        currentListInside.add(item.apply { isSelected = false })
        currentListInside.reverse()

        _listInside.postValue(currentListInside)
        _listOutside.postValue(currentListOutside)
    }

    fun start() {
        gameScope = CoroutineScope(Dispatchers.Default)
    }

    fun stop() {
        gameScope.cancel()
    }

    fun buy() {
        if (_balance.value!! >= _price.value!!) {
            changeBalance(-_price.value!!)
            _price.postValue(_price.value!! + 10)
            val currentList = _listInside.value!!.toMutableList()
            val newList = mutableListOf<RaceItem>()
            currentList.forEach {
                if (it.number != null) {
                    newList.add(it)
                }
            }
            newList.add(RaceItem(number = 1, isSelected = false))
            if (1 random 3 == 1) {
                newList.add(RaceItem(number = 1, isSelected = false, isPresent = true))
            }
            newList.add(RaceItem(number = null, isSelected = false))
            _listInside.postValue(newList)
        }
    }

    fun placeImg(x: Float, y: Float) {
        val currentOutsideList = _listOutside.value!!.toMutableList()
        val currentInsideList = _listInside.value!!.toMutableList()
        val selectedItem = currentInsideList.find { it.isSelected }!!
        selectedItem.x = x
        selectedItem.y = y
        currentOutsideList.add(selectedItem)
        currentInsideList.removeAll { it.isSelected }
        _listInside.postValue(currentInsideList)
        _listOutside.postValue(currentOutsideList)
        _placeImg.postValue(false to 1)
    }

    fun onItemClick(position: Int, item: RaceItem) {
        val currentList = _listInside.value!!.toMutableList()
        val selectedItem = currentList.find { it.isSelected }
        val newList = mutableListOf<RaceItem>()

        if (item.isPresent) {
            currentList[position].isPresent = false
            _listInside.postValue(currentList)
        } else {
            if (item.number != null) {
                if (selectedItem == null) {
                    _placeImg.postValue(true to item.number!!)
                    currentList[position].isSelected = true
                    _listInside.postValue(currentList)
                } else {
                    if (currentList[position].number == null) {
                        _placeImg.postValue(false to 1)
                        currentList[position].isSelected = false
                        _listInside.postValue(currentList)
                    } else {
                        if (currentList.indexOf(selectedItem) == position) {
                            _placeImg.postValue(false to 1)
                            currentList[position].isSelected = false
                            _listInside.postValue(currentList)
                        } else {
                            if (currentList[position].number != null && currentList[position].number != 15 &&
                                currentList[position].number == selectedItem.number
                            ) {
                                currentList[position].number = currentList[position].number!! + 1
                                currentList[currentList.indexOf(selectedItem)].number = null
                                _placeImg.postValue(false to 1)
                                currentList[currentList.indexOf(selectedItem)].isSelected = false
                                currentList.forEach {
                                    if (it.number != null) {
                                        newList.add(it)
                                    }
                                }
                                newList.add(RaceItem(number = null, isSelected = false))
                                _listInside.postValue(newList)
                            } else {
                                _placeImg.postValue(false to 1)
                                currentList[currentList.indexOf(selectedItem)].isSelected = false
                                _listInside.postValue(currentList)
                            }

                        }
                    }
                }
            }
        }
    }

    fun letPlanesMove(
        trackX: Int,
        trackY: Int,
        trackHeight: Int,
        trackWidth: Int,
        planeWidth: Int,
        planeHeight: Int,
        cornerRadius: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(5)
                val currentList = _listOutside.value!!
                val newList = mutableListOf<RaceItem>()
                currentList.forEach { plane ->
                    var riches = true
                    val middleYB =
                        (trackY + (trackHeight / 2) - 40)..(trackY + (trackHeight / 2) + 40)
                    if (plane.y.toInt() !in middleYB && plane.x > trackX + (trackWidth / 2)) {
                        riches = false
                    }

                    val leftSide = (trackX - 40)..(trackX + 40)
                    val rightSide = (trackX + trackWidth - 40)..(trackX + trackWidth + 40)
                    val topSide = (trackY - 40)..(trackY + 40)
                    val bottomSide = (trackY + trackHeight - 40)..(trackY + trackHeight + 40)

                    val isLeft = plane.x.toInt() in leftSide
                    val isRight = plane.x.toInt() + planeWidth in rightSide
                    val isTop = plane.y.toInt() in topSide
                    val isBottom = plane.y.toInt() + planeHeight in bottomSide

                    val planeSpeed = plane.speed

                    val leftTopCorner =
                        plane.x.toInt() in (trackX - 40)..(trackX + cornerRadius) && plane.y.toInt() in (trackY - 40)..(trackY + cornerRadius)
                    val leftBottomCorner =
                        plane.x.toInt() in (trackX - 40)..(trackX + cornerRadius) && (plane.y.toInt() + planeHeight) in (trackY + trackHeight - cornerRadius)..(trackY + trackHeight)
                    val rightTopCorner =
                        (plane.x.toInt() + planeWidth) in (trackX - 40 + trackWidth - cornerRadius)..(trackX + trackWidth + 40) && plane.y.toInt() in (trackY - 40)..(trackY + cornerRadius)
                    val rightBottomCorner =
                        (plane.x.toInt() + planeWidth) in (trackX + trackWidth - cornerRadius)..(trackX + trackWidth + 40) && (plane.y.toInt() + planeHeight) in (trackY + 40 + trackHeight - cornerRadius)..(trackY + trackHeight + 40)

                    if (leftTopCorner) {
                        l("leftTopCorner")
                        plane.x = plane.x - planeSpeed
                        plane.y = plane.y + planeSpeed
                    } else if (leftBottomCorner) {
                        l("leftBottomCorner")
                        plane.x = plane.x + planeSpeed
                        plane.y = plane.y + planeSpeed
                    } else if (rightTopCorner) {
                        l("rightTopCorner")
                        plane.x = plane.x - planeSpeed
                        plane.y = plane.y - planeSpeed
                    } else if (rightBottomCorner) {
                        l("rightBottomCorner")
                        plane.x = plane.x + planeSpeed
                        plane.y = plane.y - planeSpeed
                    } else {
                        if (isTop) {
                            if (isLeft) {
                                plane.y = plane.y + planeSpeed
                            } else {
                                plane.x = plane.x - planeSpeed
                            }
                        }

                        if (isLeft) {
                            if (isBottom) {
                                plane.x = plane.x + planeSpeed
                            } else {
                                plane.y = plane.y + planeSpeed
                            }
                        }

                        if (isBottom) {
                            if (isRight) {
                                plane.y = plane.y - planeSpeed
                            } else {
                                plane.x = plane.x + planeSpeed
                            }
                        }

                        if (isRight) {
                            if (isTop) {
                                plane.x = plane.x - planeSpeed
                            } else {
                                plane.y = plane.y - planeSpeed
                            }
                        }
                    }
                    val middleY =
                        (trackY + (trackHeight / 2) - 40)..(trackY + (trackHeight / 2) + 40)
                    if (plane.y.toInt() in middleY && plane.x > trackX + (trackWidth / 2) && !riches) {
                        changeBalance(plane.number!! * 100)
                    }

                    newList.add(plane)
                }
                _listOutside.postValue(newList)
            }
        }
    }

    private fun changeBalance(value: Int) {
        _balance.postValue(_balance.value!! + value)
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}