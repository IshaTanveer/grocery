package com.grocery.groceryhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grocery.groceryhub.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var databaseRef: DatabaseReference
    private  var productQuantity: Int? = 0
    lateinit var product: Product
    var new = StartNewActivity()
    private var productId: String? = null
    var actualproductQuantity: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener{
            finish()
        }

        productId = intent.getStringExtra("productId").toString()
        fetchProductData(productId!!)
        searchingCart()
        //addToCart()

    }

    private fun searchingCart() {
        binding.addToCart.setOnClickListener{
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            databaseRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("carts").child(userId!!).child("items")
            databaseRef.orderByChild("id").equalTo(productId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val intent = Intent(this@ProductDetailActivity, CartActivity::class.java)
                            intent.putExtra("actualQuantity", actualproductQuantity)
                            this@ProductDetailActivity.startActivity(intent)
                        }
                        else{
                            addToCart()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Product", "Failed to read product", error.toException())
                    }
                })
        }

    }

    private fun addToCart() {
            val Id = databaseRef.push().key
            if (Id != null) {
                product.quantity = 1
                databaseRef.child(Id).setValue(product)
                    .addOnSuccessListener {
                        Toast.makeText(this, actualproductQuantity.toString(), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ProductDetailActivity, CartActivity::class.java)
                        intent.putExtra("actualQuantity", actualproductQuantity)
                        this.startActivity(intent)
                        Snackbar.make(binding.main, "Added to cart", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener{
                        Snackbar.make(binding.main, "Error", Snackbar.LENGTH_SHORT)
                            .show()
                    }
            }
    }

    private fun fetchProductData(productId: String) {
        databaseRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("product").child(productId)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    product = snapshot.getValue(Product::class.java)!!
                    product.id = productId
                    actualproductQuantity = product.quantity
                    setData(product)
                    Log.d("Product", "Name: ${product.name}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Product", "Failed to read product", error.toException())
            }

        })
    }
    private fun setData(product: Product) {
        //binding.productPrice.setText(product.price)
        Glide.with(this)
            .load(product.photo)
            .into(binding.productPhoto)
        binding.productPrice.text = product.price.toString()
        binding.productName.text = product.name
        binding.productWeight.text = product.weight
        binding.productReviews.text = product.reviews.toString()
        binding.productDesc.text = product.description
        productQuantity = product.quantity
    }

}