package com.grocery.groceryhub

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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
    private  var myQuantity: Int? = 1
    lateinit var product: Product

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

        val productId:String = intent.getStringExtra("productId").toString()
        fetchProductData(productId)
        changeQuantity()
        addToCart()

    }

    private fun addToCart() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        binding.addToCart.setOnClickListener{
            databaseRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("carts").child(userId!!).child("items")
            val productId = databaseRef.push().key
            if (productId != null) {
                databaseRef.child(productId).setValue(product)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    private fun changeQuantity() {
        binding.increaseQuantity.setOnClickListener{
            if(myQuantity!! < productQuantity!!){
                myQuantity = myQuantity!! + 1
                binding.quantity.text = myQuantity.toString()
            }
        }
        binding.decreaseQuantity.setOnClickListener{
            if(myQuantity!! > 0){
                myQuantity = myQuantity!! - 1
                binding.quantity.text = myQuantity.toString()
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