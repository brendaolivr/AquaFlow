package com.example.aquaflow.ui.sensors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aquaflow.MainActivity
import com.example.aquaflow.data.AppDatabase
import com.example.aquaflow.databinding.FragmentSensorsBinding
import com.example.aquaflow.model.Sensor
import kotlinx.coroutines.launch

class SensorsFragment : Fragment() {

    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SensorsAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        adapter = SensorsAdapter()
        binding.recyclerSensors.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSensors.adapter = adapter

        binding.btnMenuSensors.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.btnBackSensors.setOnClickListener {
            (activity as? MainActivity)?.navigateToHome()
        }

        loadSensors()
    }

    private fun loadSensors() {
        viewLifecycleOwner.lifecycleScope.launch {
            val sensors: List<Sensor> = db.sensorDao().getAllSensors()
            adapter.submitList(sensors)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}