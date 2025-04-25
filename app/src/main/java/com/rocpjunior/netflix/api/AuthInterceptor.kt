package com.rocpjunior.netflix.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        //1 - Acessar a Requisicão
        val construtorRequisicao =  chain.request().newBuilder()

        //2 - Alterar a URL ou a Rota/EndPoint da requisicão
        val urlAtual = chain.request().url()
        val novaUrl = urlAtual.newBuilder()
        novaUrl.addQueryParameter("api_key", RetrofitService.API_KEY)

        //3 - Configurar a nova URL na requisicão
        construtorRequisicao.url(novaUrl.build())

        return chain.proceed(construtorRequisicao.build())
    }
}