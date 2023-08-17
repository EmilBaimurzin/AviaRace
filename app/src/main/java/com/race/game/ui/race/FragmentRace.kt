package com.race.game.ui.race

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.race.game.R
import com.race.game.databinding.FragmentRaceBinding
import com.race.game.domain.race.RaceAdapter
import com.race.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentRace : ViewBindingFragment<FragmentRaceBinding>(FragmentRaceBinding::inflate) {
    private lateinit var raceAdapter: RaceAdapter
    private val viewModel: RaceViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        viewModel.listInside.observe(viewLifecycleOwner) {
            raceAdapter.items = it
            raceAdapter.notifyDataSetChanged()
            lifecycleScope.launch(Dispatchers.Main) {
                val listInside = viewModel.listInside.value!!
                val listOutside = viewModel.listOutside.value!!
                val levelList = mutableListOf<Int>()

                listInside.forEach { item ->
                    levelList.add(item.number ?: 1)
                }

                listOutside.forEach { item ->
                    levelList.add(item.number ?: 1)
                }

                delay(20)

                binding.level.text = (levelList.maxOrNull() ?: 1).toString()
            }
        }

        binding.home.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            delay(20)
            viewModel.start()
            viewModel.letPlanesMove(
                binding.track.x.toInt(),
                binding.track.y.toInt(),
                binding.track.height,
                binding.track.width,
                dpToPx(90),
                dpToPx(50),
                dpToPx(100)
            )
        }

        binding.buy.setOnClickListener {
            viewModel.buy()
        }

        viewModel.balance.observe(viewLifecycleOwner) {
            binding.balance.text = it.toString()
        }

        viewModel.price.observe(viewLifecycleOwner) {
            binding.price.text = it.toString()
        }

        binding.placeImg.setOnClickListener {
            viewModel.placeImg(binding.track.x, binding.track.y + binding.track.height / 2)
            lifecycleScope.launch(Dispatchers.Main) {
                val listInside = viewModel.listInside.value!!
                val listOutside = viewModel.listOutside.value!!
                val levelList = mutableListOf<Int>()

                listInside.forEach { item ->
                    levelList.add(item.number ?: 1)
                }

                listOutside.forEach { item ->
                    levelList.add(item.number ?: 1)
                }

                delay(20)

                binding.level.text = (levelList.maxOrNull() ?: 1).toString()
            }
        }

        viewModel.placeImg.observe(viewLifecycleOwner) {
            binding.placeImg.isVisible = it.first
            val img = when (it.second) {
                1 -> R.drawable.air01
                2 -> R.drawable.air02
                3 -> R.drawable.air03
                4 -> R.drawable.air04
                5 -> R.drawable.air05
                6 -> R.drawable.air06
                7 -> R.drawable.air07
                8 -> R.drawable.air08
                9 -> R.drawable.air09
                10 -> R.drawable.air10
                11 -> R.drawable.air11
                12 -> R.drawable.air12
                13 -> R.drawable.air13
                14 -> R.drawable.air14
                else -> R.drawable.air15
            }
            binding.placeImg.setImageResource(img)
        }

        viewModel.listOutside.observe(viewLifecycleOwner) {
            binding.planesLayout.removeAllViews()
            val countList = it.map { it.number }
            var amount = 0
            countList.forEach {
                amount += it ?: 0
            }
            binding.speed.text = (amount * 100).toString() + "/C"
            it.forEach { plane ->
                val planeView = ImageView(requireContext())
                planeView.layoutParams = ViewGroup.LayoutParams(dpToPx(90), dpToPx(50))
                val img = when (plane.number) {
                    1 -> R.drawable.air01
                    2 -> R.drawable.air02
                    3 -> R.drawable.air03
                    4 -> R.drawable.air04
                    5 -> R.drawable.air05
                    6 -> R.drawable.air06
                    7 -> R.drawable.air07
                    8 -> R.drawable.air08
                    9 -> R.drawable.air09
                    10 -> R.drawable.air10
                    11 -> R.drawable.air11
                    12 -> R.drawable.air12
                    13 -> R.drawable.air13
                    14 -> R.drawable.air14
                    else -> R.drawable.air15
                }
                planeView.setImageResource(img)
                planeView.x = plane.x
                planeView.y = plane.y
                binding.planesLayout.addView(planeView)

                planeView.setOnTouchListener { view, motionEvent ->
                    viewModel.stop()
                    if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE)
                        viewModel.removeFromRace(plane)
                    viewModel.start()
                    viewModel.letPlanesMove(
                        binding.track.x.toInt(),
                        binding.track.y.toInt(),
                        binding.track.height,
                        binding.track.width,
                        dpToPx(90),
                        dpToPx(50),
                        dpToPx(100)
                    )
                    true
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun initAdapter() {
        raceAdapter = RaceAdapter { position, item ->
            viewModel.onItemClick(position, item)
        }
        with(binding.gameRV) {
            adapter = raceAdapter
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                orientation = GridLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}