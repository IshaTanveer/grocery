package com.grocery.groceryhub

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.grocery.groceryhub.databinding.CartRvLayoutBinding

class CartRVAdapter(
    private var product:ArrayList<Product>,
    var context: Context,
    private var actualQuantity: Int
): RecyclerView.Adapter<CartRVAdapter.myViewHolder>() {
    class myViewHolder(var binding: CartRvLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val binding = CartRvLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return myViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return product.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.binding.productName.text = product[position].name
        holder.binding.productPrice.text = product[position].price.toString()
        holder.binding.productWeight.text = product[position].weight
        holder.binding.quantity.text = product[position].quantity.toString()
        Glide.with(holder.itemView.context)
            .load(product[position].photo)
            .into(holder.binding.productPhoto)

        var myQuantity: Int? = product[position].quantity
        holder.binding.increaseQuantity.setOnClickListener{
            if(myQuantity!! < actualQuantity){
                myQuantity = myQuantity!! + 1
                updateQuantityInFirebase(product[position].id, myQuantity!!, position, holder)
            }
            else{
                Toast.makeText(context, "no more", Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding.decreaseQuantity.setOnClickListener{
            if(myQuantity!! > 1){
                myQuantity = myQuantity!! - 1
                updateQuantityInFirebase(product[position].id, myQuantity!!, position, holder)
            }
            else{
                removeProductFromCart(product[position].id,position, holder )
            }
        }

    }

    private fun removeProductFromCart(id: String, position: Int, holder: CartRVAdapter.myViewHolder) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("carts").child(userId!!).child("items")
            .child(id)
        databaseRef.removeValue().addOnSuccessListener {
            //product.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, product.size)
            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "can't remove the product", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateQuantityInFirebase(id: String, myQuantity: Int, position: Int, holder: CartRVAdapter.myViewHolder) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance("https://groceryhub1-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("carts").child(userId!!).child("items")
            .child(id)
            .child("quantity")
        databaseRef.setValue(myQuantity)
        Toast.makeText(context, myQuantity.toString(), Toast.LENGTH_SHORT).show()
        holder.binding.quantity.text = myQuantity.toString()
    }

}