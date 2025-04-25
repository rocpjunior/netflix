package com.rocpjunior.netflix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rocpjunior.netflix.adaptador.FilmeAdapter
import com.rocpjunior.netflix.api.RetrofitService
import com.rocpjunior.netflix.databinding.ActivityMainBinding
import com.rocpjunior.netflix.modelo.FilmeRecente
import com.rocpjunior.netflix.modelo.FilmeResposta
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var paginaAtual = 1
    var jobFilmeRecente: Job? = null
    var jobFilmesPopulares: Job? = null
    var gridLayoutManager: GridLayoutManager? = null
    private lateinit var filmeAdapter: FilmeAdapter

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarViews()
    }

    private fun inicializarViews() {
        filmeAdapter = FilmeAdapter { filme ->
            val intent  = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("filme", filme)
            startActivity(intent)
        }
        binding.rvPopulares.adapter = filmeAdapter
        gridLayoutManager = GridLayoutManager(this, 2)

        binding.rvPopulares.layoutManager = gridLayoutManager

        binding.rvPopulares.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val descendo = recyclerView.canScrollVertically(1)
                if(!descendo){
                    proximaPagina()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        recuperarFilmeRecente()
        recuperarFilmesPopulares()
    }

    private fun recuperarFilmeRecente() {
        jobFilmeRecente = CoroutineScope(Dispatchers.IO).launch {
            var resposta: retrofit2.Response<FilmeRecente>? = null

            try {
                resposta = filmeAPI.recuperarFilmeRecente()
            } catch (e: Exception) {
                exibirMensagem("Erro na requisicão")
            }

            if (resposta != null) {
                if (resposta.isSuccessful) {

                    val filmeRecente = resposta.body()
                    val nomeImagem = filmeRecente?.poster_path
                    //val titulo = filmeRecente?.title
                    val url = RetrofitService.URL_BASE_IMAGENS + "W780" + nomeImagem

                    withContext(Dispatchers.Main) {
                        //val texto = "Título: $titulo URL: $url"
                        //binding.textPopulares.text = texto
                        Picasso.get()
                            .load(url)
                            .error(R.drawable.capa)
                            .into(binding.imgCapa)
                    }
                } else {
                    exibirMensagem("Não foi possível recuperar o filme mais recente: CÓDIGO DE ERRO: ${resposta.code()}")
                }
            } else {
                exibirMensagem("Não foi possível fazer a requisicão D=")
            }
        }
    }

    private fun proximaPagina(){

        if(paginaAtual < 1000){
            paginaAtual++
            recuperarFilmesPopulares(paginaAtual)
        }
    }

    private fun recuperarFilmesPopulares(pagina: Int = 1) {
        jobFilmesPopulares = CoroutineScope(Dispatchers.IO).launch {
            var resposta: retrofit2.Response<FilmeResposta>? = null

            try {
                resposta = filmeAPI.recuperarFilmesPopulares(pagina)
            } catch (e: Exception) {
                exibirMensagem("Erro na requisicão")
            }

            if (resposta != null) {
                if (resposta.isSuccessful) {

                    val filmeResposta = resposta.body()
                    val listaFilmes = filmeResposta?.filmes
                    if (listaFilmes != null && listaFilmes.isNotEmpty()) {
                            withContext(Dispatchers.Main){
                                filmeAdapter.adicionarLista(listaFilmes)
                        }
                    }

                }
            }
        }
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(
            applicationContext,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onStop() {
        super.onStop()
        jobFilmeRecente?.cancel()
        jobFilmesPopulares?.cancel()
    }
}

