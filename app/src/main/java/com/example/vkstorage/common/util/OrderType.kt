package com.example.vkstorage.common.util

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}
