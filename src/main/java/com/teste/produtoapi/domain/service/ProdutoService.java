package com.teste.produtoapi.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teste.produtoapi.api.model.Produto;
import com.teste.produtoapi.api.repository.ProdutoRepository;
import com.teste.produtoapi.domain.exception.ProdutoNaoEncontradoException;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	public Produto salvar(Produto produto) {
		return produtoRepository.save(produto);
	}
	
	public void remover(String produtoId) {
		
		buscarOuFalhar(produtoId);
		produtoRepository.deleteById(produtoId);		
		
	}
	
	public Produto buscarOuFalhar(String produtoId) {
		return produtoRepository.findById(produtoId)
				.orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));
	}
	
}
