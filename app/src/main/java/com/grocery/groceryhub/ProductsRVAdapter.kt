package com.grocery.groceryhub

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.groceryhub.databinding.ProductsRvBinding

class ProductsRVAdapter(
    private var product:ArrayList<Product>,
    var context: Context,
    var new:StartNewActivity
): RecyclerView.Adapter<ProductsRVAdapter.myViewHolder>() {
    class myViewHolder(var binding: ProductsRvBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val binding = ProductsRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return myViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return product.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.binding.productName.text = product[position].name
        holder.binding.productPrice.text = product[position].price.toString()
        holder.binding.productWeight.text = product[position].weight
        Glide.with(holder.itemView.context)
            .load(product[position].photo)
            .into(holder.binding.productPhoto)

        holder.binding.productPhoto.setOnClickListener{
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("productId", product[position].id)
            context.startActivity(intent)
        }
    }
}