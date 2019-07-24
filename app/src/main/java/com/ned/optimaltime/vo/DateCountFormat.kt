package com.ned.optimaltime.vo

import java.time.LocalDateTime

/**
 * Use to return query result with <code>date</code> and an <code>Int</code> as a POJO
 */
data class DateCountFormat(
    val date: LocalDateTime,
    val count: Int
) {

}