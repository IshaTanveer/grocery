package com.grocery.groceryhub

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.groceryhub.databinding.CategoriesRvLayoutBinding
import com.grocery.groceryhub.databinding.FragmentHomeBinding
import java.lang.reflect.Array
import kotlin.contracts.contract

class CategoriesRVAdapter(
    var categories: ArrayList<String>,
    var categoryIcons: ArrayList<Int>,
    var context: Context
):RecyclerView.Adapter<CategoriesRVAdapter.myViewHolder>() {
    class myViewHolder(var binding:CategoriesRvLayoutBinding):RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val binding = CategoriesRvLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return myViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.binding.categoryName.text = categories[position]
        holder.binding.categoryPhoto.setImageResource(categoryIcons[position])
    }
}