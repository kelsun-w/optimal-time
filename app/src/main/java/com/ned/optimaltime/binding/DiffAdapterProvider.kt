package com.ned.optimaltime.binding

import androidx.recyclerview.widget.DiffUtil
import com.ned.optimaltime.vo.AppEntity

class DiffAdapterProvider{

    fun <T : AppEntity> setupCallback () : DiffUtil.ItemCallback<T>{
        return object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.isSame(newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.hasSameContent(newItem)
            }
        }
    }
}