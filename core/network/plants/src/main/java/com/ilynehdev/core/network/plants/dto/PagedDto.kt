package com.ilynehdev.core.network.plants.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedDto<T>(
    @SerialName("data") val data: List<T>,
    @SerialName("to") val to: Int? = null,
    @SerialName("per_page") val perPage: Int,
    @SerialName("current_page") val currentPage: Int,
    @SerialName("from") val from: Int? = null,
    @SerialName("last_page") val lastPage: Int,
    @SerialName("total") val total: Int,
) {
    /** Next page number, or null on last page. Derived from server metadata, never item count. */
    val nextPage: Int? get() = if (currentPage < lastPage) currentPage + 1 else null
}