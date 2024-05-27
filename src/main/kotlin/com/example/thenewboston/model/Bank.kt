package com.example.thenewboston.model

import com.fasterxml.jackson.annotation.JsonProperty


data class Bank(
    val accountNumber: String,
    val trust: Double,
    val transactionFee: Int
)