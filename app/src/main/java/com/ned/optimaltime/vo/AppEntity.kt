package com.ned.optimaltime.vo

interface AppEntity {
    var uid : Long

    fun isSame(other : AppEntity) : Boolean
    fun hasSameContent(other: AppEntity) :Boolean
}