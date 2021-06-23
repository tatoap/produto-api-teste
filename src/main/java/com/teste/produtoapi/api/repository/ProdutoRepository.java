package com.teste.produtoapi.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.teste.produtoapi.api.model.Produto;
import com.teste.produtoapi.api.repository.produto.ProdutoRepositoryQuery;

public interface ProdutoRepository extends MongoRepository<Produto, String>, ProdutoRepositoryQuery {

	@Query(value = "{ 'nome': {$regex : ?0, $options: 'i'} }")
	List<Produto> findByNome(String nome);
	
	@Query(value = "{ 'dataCriacao': { $gte: ?0, $lte: ?1 } }")
	List<Produto> findByData(LocalDate dataCriacaoDe, LocalDate dataCriacaoAte);
	
}
