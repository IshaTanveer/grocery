package com.grocery.groceryhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grocery.groceryhub.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var productList: ArrayList<Product>
    private lateinit var databaseRef: DatabaseReference
    private var price: Int? = 0
    lateinit var myAdapter: CartRVAdapter
    var new = StartNewActivity()
    var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener{
            finish()
        }
        val layoutManager = LinearLayoutManager(this@CartActivity)
        binding.cartRecyclerView.layoutManager=layoutManager
        val actualQuantity = intent.getIntExtra("actualQuantity", 1)

        productList = arrayListOf()
        myAdapter = CartRVAdapter(productList, this@CartActivity, actualQuantity)
        binding.cartRecyclerView.adapter = myAdapter

        fetchData(actualQuantity)
        checkout()

    }

    private fun calculateTotalPrice() {
        price = 0
        for (product in productList) {
            val itemPrice = (product.price ?: 0)
            val itemQuantity = (product.quantity ?: 0)
            price = price?.plus(itemPrice * itemQuantity)
        }
        var shippingCharges = 0
        binding.price.text = "Rs. $price"
        if (price!! > 0){
            shippingCharges = 50
        }
        else{
            binding.nothingInCart.setText("Your cart is empty")
        }
        binding.shippingPrice.text = "Rs. $shippingCharges"
        total = price!! + shippingCharges
        binding.totalPrice.text = "Rs. $total"
    }

    private fun checkout() {
        binding.checkout.setOnClickListener{
            val intent = Intent(this@CartActivity, AddressActivity::class.java)
            intent.putExtra("totalPrice", total)
            this.startActivity(intent)
            Toast.makeText(this@CartActivity, "checkout", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchData(actualQuantity: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        binding.cartRecyclerView.visibility = View.GONE
        //binding.productsProgressbar.visibility = View.VISIBLE
        databaseRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("carts").child(userId!!).child("items")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                if(snapshot.exists()){
                    for (productSnap in snapshot.children){
                        val product = productSnap.getValue(Product::class.java)
                        if (product != null) {
                            product.id = productSnap.key.toString()
                            productList.add(product)
                            Log.d("CartActivity", "Fetched product: ${product.name}, quantity: ${product.quantity}")
                        }
                        else{
                            Log.e("Firebase", "Product is null at: ${productSnap.key}")
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                    binding.cartRecyclerView.visibility = View.VISIBLE
                    //binding.productsProgressbar.visibility = View.GONE
                }
                calculateTotalPrice()
            }
            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.main, "Error", Snackbar.LENGTH_SHORT)
                    .show()
            }
        })
    }

}