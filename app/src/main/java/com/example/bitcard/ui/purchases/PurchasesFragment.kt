package com.example.bitcard.ui.purchases

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcard.adapters.OnTileClickedListener
import com.example.bitcard.adapters.PurchaseRecyclerModel
import com.example.bitcard.adapters.PurchasesRecyclerViewAdapter
import com.example.bitcard.databinding.ShopsFragmentBinding
import com.example.bitcard.network.daos.responses.models.Purchase

class PurchasesFragment : Fragment(), OnTileClickedListener<PurchaseRecyclerModel> {

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
            val hashMap = LinkedHashMap<Long, List<Purchase>>()
            var tmpId = purchases[0].tokens_id
            var tmpList = ArrayList<Purchase>()
            for(p : Purchase in purchases){
                if(tmpId == p.tokens_id){
                    tmpList.add(p)
                }else{
                    hashMap[tmpId] = tmpList
                    tmpList = ArrayList()
                    tmpId = p.tokens_id
                    tmpList.add(p)
                }
            }
            for (key : Long in hashMap.keys){
                hashMap[key]?.let {
                    addPurchaseToAdapter(
                        PurchaseRecyclerModel(it))
                }
            }
        }
    }



    private fun addPurchaseToAdapter(purchases: PurchaseRecyclerModel){
        requireActivity().runOnUiThread{
            adapter.add(purchases)
        }
    }

    override fun onClick(adapterPosition: Int, model: PurchaseRecyclerModel) {
        val intent = Intent(requireContext(), PurchaseInfoActivity::class.java)
        intent.putExtra("purchase_ids", model.getProductsIds().toLongArray())
        intent.putExtra("token_id", model.getTokenId())
        startActivity(intent)
    }
}