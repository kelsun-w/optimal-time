package com.ned.optimaltime.model.entity

interface AppEntity {
    var uid : Long

    fun isSame(other : AppEntity) : Boolean
    fun hasSameContent(other: AppEntity) :Boolean
}