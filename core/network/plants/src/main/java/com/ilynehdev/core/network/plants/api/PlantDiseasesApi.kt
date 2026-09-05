package com.ilynehdev.core.network.plants.api

import com.ilynehdev.core.network.plants.dto.PagedDto
import com.ilynehdev.core.network.plants.dto.PlantDiseaseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface PlantDiseasesApi {
    suspend fun getPlantDiseases(page: Int): Page<PlantDiseaseDto>
}

class PlantDiseaseApiImpl(private val client: HttpClient) : PlantDiseasesApi {
    override suspend fun getPlantDiseases(page: Int): Page<PlantDiseaseDto> {
        return client.get("pest-disease-list") {
            parameter("page", page)
        }.body<PagedDto<PlantDiseaseDto>>().let {
            Page(items = it.data, nextKey = it.nextPage)
        }
    }
}