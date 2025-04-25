package com.rocpjunior.netflix.api

import com.rocpjunior.netflix.modelo.FilmeRecente
import com.rocpjunior.netflix.modelo.FilmeResposta
import retrofit2.http.GET
import retrofit2.http.Query

interface FilmeAPI {
    @GET("movie/latest")
    suspend fun recuperarFilmeRecente(): retrofit2.Response<FilmeRecente>

    @GET("movie/popular")
    suspend fun recuperarFilmesPopulares(@Query("page") pagina: Int): retrofit2.Response<FilmeResposta>
}