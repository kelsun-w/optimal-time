package com.ned.optimaltime.binding

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ned.optimaltime.model.entity.AppEntity

abstract class DiffAdapter <T : AppEntity, VH: RecyclerView.ViewHolder> : ListAdapter<T, VH>(callbackProducer.setupCallback<T>()) {

    companion object {
        val callbackProducer = DiffAdapterProvider()
    }
}