package com.stho.mysensorcolors.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.stho.mysensorcolors.MainViewModel
import com.stho.mysensorcolors.R
import com.stho.mysensorcolors.databinding.FragmentDashboardBinding
import java.text.DecimalFormat

class DashboardFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentDashboardBinding
    private val df = DecimalFormat("0.00")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity()).get<MainViewModel>(MainViewModel::class.java)
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel.colorLD.observe(viewLifecycleOwner, { color -> observeColor(color) })
        viewModel.valueLD.observe(viewLifecycleOwner, { value -> binding.value.text = df.format(value) })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.mode = MainViewModel.Mode.Gyroscope
    }

    private fun observeColor(color: Int) {
        requireActivity().window.decorView.setBackgroundColor(color);
    }
}