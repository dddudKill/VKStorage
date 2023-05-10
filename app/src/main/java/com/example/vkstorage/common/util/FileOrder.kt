package com.example.vkstorage.common.util

sealed class FileOrder(val orderType: OrderType) {
    class Title(orderType: OrderType) : FileOrder(orderType)
    class Date(orderType: OrderType) : FileOrder(orderType)
    class Size(orderType: OrderType) : FileOrder(orderType)
    class Extension(orderType: OrderType) : FileOrder(orderType)

    fun copy(orderType: OrderType): FileOrder {
        return when (this) {
            is Title -> Title(orderType)
            is Date -> Date(orderType)
            is Size -> Size(orderType)
            is Extension -> Extension(orderType)
        }
    }
}
