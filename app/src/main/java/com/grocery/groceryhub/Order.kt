package com.grocery.groceryhub

data class Order(
    val userId: String = "",
    val customerName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val area: String = "",
    val totalAmount: Int = 0,
    val paymentMethod: String = "",
    val orderStatus: String = "Pending",
    val timestamp: Long = System.currentTimeMillis(),
    var productList: List<Product>
)
