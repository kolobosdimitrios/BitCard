package com.example.bitcard.ui.purchases

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.PurchaseRecyclerModel
import com.example.bitcard.adapters.PurchasesRecyclerViewAdapter
import com.example.bitcard.databinding.ShopsFragmentBinding
import com.example.bitcard.network.daos.responses.models.Purchase

class PurchasesFragment : Fragment(), OnTileClickedListener<Purchase> {

    private lateinit var binder: ShopsFragmentBinding
    private lateinit var adapter : PurchasesRecyclerViewAdapter
    private val purchasesFragmentViewModel : PurchasesFragmentViewModel by activityViewModels() {defaultViewModelProviderFactory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = ShopsFragmentBinding.inflate(inflater)
        adapter = PurchasesRecyclerViewAdapter(
            requireContext(),
            this,
            ArrayList()
        )

        binder.shopsRecycler.setHasFixedSize(true)
        binder.shopsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binder.shopsRecycler.adapter = adapter

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        purchasesFragmentViewModel.selectedPurchases.observe(viewLifecycleOwner){ purchases ->
            requireActivity().runOnUiThread {
                adapter.add(purchases)
            }
        }
    }


    override fun onClick(adapterPosition: Int, model: Purchase) {
        val intent = Intent(requireContext(), PurchaseInfoActivity::class.java)
        intent.putExtra("purchase_id", model.id)
        startActivity(intent)
    }
}