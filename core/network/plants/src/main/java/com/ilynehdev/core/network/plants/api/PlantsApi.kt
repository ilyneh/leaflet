package com.ilynehdev.core.network.plants.api

import com.ilynehdev.core.network.plants.dto.PagedDto
import com.ilynehdev.core.network.plants.dto.PlantDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface PlantsApi {
    suspend fun getPlants(page: Int, query: String? = null): Page<PlantDto>
    suspend fun getPlant(id: Long): PlantDto
}

class PlantsApiImpl(private val client: HttpClient) : PlantsApi {
    override suspend fun getPlants(page: Int, query: String?): Page<PlantDto> {
        return client.get("v2/species-list") {
            parameter("page", page)
            if (!query.isNullOrBlank()) parameter("q", query)
        }.body<PagedDto<PlantDto>>().let {
            Page(items = it.data, nextKey = it.nextPage)
        }
    }

    override suspend fun getPlant(id: Long): PlantDto {
        return client.get("v2/species/details/$id").body()
    }
}

data class Page<T>(val items: List<T>, val nextKey: Int?)
