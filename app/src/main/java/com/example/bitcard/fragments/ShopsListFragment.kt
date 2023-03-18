package com.example.bitcard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.ShopsListRecycler
import com.example.bitcard.databinding.FragmentShopsListBinding
import com.example.bitcard.network.daos.responses.models.Shop
import com.example.bitcard.view_models.ShopViewModel
import com.example.bitcard.view_models.ShopsViewModel

class ShopsListFragment : Fragment(), OnTileClickedListener<Shop> {


    private val shopViewModel: ShopViewModel by activityViewModels() {defaultViewModelProviderFactory}
    private val shopsViewModel: ShopsViewModel by activityViewModels() {defaultViewModelProviderFactory}
    private lateinit var binding: FragmentShopsListBinding
    private lateinit var adapter: ShopsListRecycler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentShopsListBinding.inflate(layoutInflater)

        this.adapter = ShopsListRecycler(context = requireContext(), onTileClickedListener = this)
        this.binding.shopsRecycler.layoutManager = LinearLayoutManager(requireContext())
        this.binding.shopsRecycler.setHasFixedSize(false)
        this.binding.shopsRecycler.adapter = adapter

        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopsViewModel.selectedItemsList.observe(viewLifecycleOwner) { shops ->
            this.adapter.updateData(shops)
        }

    }

    override fun onClick(adapterPosition: Int, model: Shop) {
        shopViewModel.selectItem(model)
    }

}