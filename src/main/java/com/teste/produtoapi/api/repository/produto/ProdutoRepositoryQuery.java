package com.teste.produtoapi.api.repository.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.teste.produtoapi.api.model.Produto;
import com.teste.produtoapi.api.repository.filter.ProdutoFilter;

public interface ProdutoRepositoryQuery {
		
	Page<Produto> filtrar(ProdutoFilter produtoFilter, Pageable pageable);

}
