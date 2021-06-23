package com.teste.produtoapi.api.repository.produto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.teste.produtoapi.api.model.Produto;
import com.teste.produtoapi.api.repository.ProdutoRepository;
import com.teste.produtoapi.api.repository.filter.ProdutoFilter;

public class ProdutoRepositoryImpl implements ProdutoRepositoryQuery {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Override
	public Page<Produto> filtrar(ProdutoFilter produtoFilter, Pageable pageable) {
				
		List<Produto> produtos = new ArrayList<>();
		
		Query query = new Query();
		
		if (!StringUtils.isEmpty(produtoFilter.getNome())) {
			produtos = produtoRepository.findByNome(produtoFilter.getNome());
		}
			
		if (produtoFilter.getDataCriacaoDe() != null && produtoFilter.getDataCriacaoAte() != null) {
			query.addCriteria(Criteria.where("dataCriacao").gte(produtoFilter.getDataCriacaoDe()).lte(produtoFilter.getDataCriacaoAte()));
		} else if (produtoFilter.getDataCriacaoDe() != null) {
			query.addCriteria(Criteria.where("dataCriacao").gte(produtoFilter.getDataCriacaoDe()));
		} else if (produtoFilter.getDataCriacaoAte() != null) {
			query.addCriteria(Criteria.where("dataCriacao").lte(produtoFilter.getDataCriacaoAte()));
		} else {
			query.fields();
		}
				
		Pageable pageableRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
		query.with(pageableRequest);
		produtos = mongoTemplate.find(query, Produto.class);
				
		return PageableExecutionUtils.getPage(produtos, pageableRequest, () -> mongoTemplate.count(query, Produto.class));
		
	}
	
}
