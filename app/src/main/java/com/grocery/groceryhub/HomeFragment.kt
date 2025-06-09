package com.grocery.groceryhub

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.grocery.groceryhub.databinding.FragmentHomeBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private var categories = arrayListOf<String>()
    private var categoryIcons = arrayListOf<Int>()
    lateinit var adapter: CategoriesRVAdapter
    var new = StartNewActivity()
    private lateinit var productList: ArrayList<Product>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        addDataInLists()
        setCategoriesAdapter()


        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productRV.layoutManager=layoutManager
        fetchData()

    }

    private fun fetchData() {
        binding.productRV.visibility = View.GONE
        binding.productsProgressbar.visibility = View.VISIBLE
        productList = arrayListOf<Product>()
        databaseRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("product")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                if(snapshot.exists()){
                    for (productSnap in snapshot.children){
                        val product = productSnap.getValue(Product::class.java)
                        if (product != null) {
                            product.id = productSnap.key.toString()
                            productList.add(product)
                        }
                        else{
                            Log.e("Firebase", "Product is null at: ${productSnap.key}")
                        }
                    }
                    val myAdapter = ProductsRVAdapter(productList, requireContext(), new)
                    binding.productRV.adapter = myAdapter
                    binding.productRV.visibility = View.VISIBLE
                    binding.productsProgressbar.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error 33", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setCategoriesAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.categoryRV.layoutManager=layoutManager

        adapter = CategoriesRVAdapter(categories, categoryIcons, requireContext())
        binding.categoryRV.adapter = adapter
    }

    private fun addDataInLists() {
        categories.add("Vegetables")
        categories.add("Fruits")
        categories.add("Beverages")
        categories.add("Grocery")
        categories.add("Edible Oil")
        categories.add("Household")
        categories.add("Baby Care")

        categoryIcons.add(R.drawable.vegetable)
        categoryIcons.add(R.drawable.fruits)
        categoryIcons.add(R.drawable.beverages)
        categoryIcons.add(R.drawable.groceryy)
        categoryIcons.add(R.drawable.oil)
        categoryIcons.add(R.drawable.household)
        categoryIcons.add(R.drawable.babycare)
    }
}