package com.ilynehdev.core.network.plants.api

import com.ilynehdev.core.network.plants.dto.PagedDto
import com.ilynehdev.core.network.plants.dto.PlantDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface PlantsApi {
    // Filter params take a single value each; the API has no multi-value form
    // (comma-separated matches nothing, repeated params last-one-wins).
    suspend fun getPlants(
        page: Int,
        query: String? = null,
        sunlight: String? = null,
        watering: String? = null,
        poisonous: Boolean? = null,
        indoor: Boolean? = null,
    ): Page<PlantDto>

    suspend fun getPlant(id: Long): PlantDto
}

class PlantsApiImpl(private val client: HttpClient) : PlantsApi {
    override suspend fun getPlants(
        page: Int,
        query: String?,
        sunlight: String?,
        watering: String?,
        poisonous: Boolean?,
        indoor: Boolean?,
    ): Page<PlantDto> {
        return client.get("v2/species-list") {
            parameter("page", page)
            if (!query.isNullOrBlank()) parameter("q", query)
            sunlight?.let { parameter("sunlight", it) }
            watering?.let { parameter("watering", it) }
            poisonous?.let { parameter("poisonous", if (it) 1 else 0) }
            indoor?.let { parameter("indoor", if (it) 1 else 0) }
        }.body<PagedDto<PlantDto>>().let {
            Page(items = it.data, nextKey = it.nextPage)
        }
    }

    override suspend fun getPlant(id: Long): PlantDto {
        return client.get("v2/species/details/$id").body()
    }
}

data class Page<T>(val items: List<T>, val nextKey: Int?)
