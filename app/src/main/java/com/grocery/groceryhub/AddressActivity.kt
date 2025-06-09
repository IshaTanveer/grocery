package com.grocery.groceryhub

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grocery.groceryhub.databinding.ActivityAddressBinding


class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var productList: ArrayList<Product>

    private lateinit var address: String
    private lateinit var number: String
    private var price: Int = 0
    private lateinit var order: Order
    private var area: String? = null
    private var quantites: Int = 0
    private var actualQuantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener{
            finish()
        }
        price = intent.getIntExtra("totalPrice", 0)
        productList = arrayListOf()
        manageAreaSpinner()
        placeOrder()

    }

    private fun placeOrder() {
        binding.placeOrder.setOnClickListener{
            address = binding.address.text.toString()
            number = binding.number.text.toString()
            if (address.isEmpty() || number.isEmpty() || area.isNullOrEmpty())
                Toast.makeText(this, "One of the required fields is empty", Toast.LENGTH_SHORT).show()
            else{
                populateOrder()
                pushDataInOrder()
            }
        }
    }

    private fun deleteCart(){
        Log.d("Cart", "deleteCart() called")
        val dbRef: DatabaseReference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        dbRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("carts").child(userId!!).child("items")
        dbRef.removeValue().addOnSuccessListener {
            Log.d("Cart", "Cart deleted successfully")
            Toast.makeText(this, "Cart Deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Log.d("Cart", "Cart deletion failed")
            Toast.makeText(this, "Cart Deletion Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pushDataInOrder() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Orders").child(userId!!).child("order")
        val Id = databaseRef.push().key
        if (Id != null) {
            databaseRef.child(Id).setValue(order)
                .addOnSuccessListener {
                    fetchData(Id)
                    //Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Snackbar.make(binding.main, "Error", Snackbar.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun populateOrder() {
        order = FirebaseAuth.getInstance().currentUser?.uid?.let {
            Order(
                userId = it,
                customerName = "",
                phoneNumber = number,
                address = address,
                area = area ?: "",
                totalAmount = price,
                paymentMethod = "COD",
                productList = emptyList()
            )
        }!!
    }

    private fun fetchData(Id: String) {
        var dbRef: DatabaseReference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        dbRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("carts").child(userId!!).child("items")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                if(snapshot.exists()){
                    for (productSnap in snapshot.children){
                        val product = productSnap.getValue(Product::class.java)
                        if (product != null) {
                            //product.id = productSnap.key.toString()
                            pushProductsDataInOrder(Id, product)
                            quantites = product.quantity
                            productList.add(product)
                            updateQuanity(product.id)
                            //getActualQuantity(product.id)
                            Log.d("CartActivity", "Fetched product: ${product.name}, quantity: ${product.quantity}")
                        }
                        else{
                            Log.e("Firebase", "Product is null at: ${productSnap.key}")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.main, "Error", Snackbar.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun updateQuanity(Id: String){
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("product").child(Id)
            .child("quantity")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentQuantity = snapshot.getValue(Int::class.java) ?: 0
                val updatedQuantity = currentQuantity - quantites
                databaseRef.setValue(updatedQuantity)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpdateQuantity", "Failed to read quantity", error.toException())
            }
        })
    }

    private fun pushProductsDataInOrder(Id: String, product: Product){
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val productListRef = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Orders")
            .child(userId)
            .child("order")
            .child(Id)
            .child("productList")
        val productId = productListRef.push().key
        if (productId != null) {
            productListRef.child(productId).setValue(product)
                .addOnSuccessListener {
                    deleteCart()
                    displaySuccessDialog()
                    Toast.makeText(this, quantites.toString(), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add products", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun displaySuccessDialog() {
        if (!isFinishing && !isDestroyed){
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.order_success_layout)
            dialog.setCancelable(true)
            dialog.show()
        }
    }

    private fun manageAreaSpinner() {
        val areas = resources.getStringArray(R.array.areas_array)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, areas)
        binding.area.setAdapter(adapter)

        binding.area.setOnItemClickListener { parent, view, position, id ->
            area = parent.getItemAtPosition(position).toString()
        }
    }

}