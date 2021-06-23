package com.teste.produtoapi.api.controller;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.teste.produtoapi.api.model.Produto;
import com.teste.produtoapi.api.repository.ProdutoRepository;
import com.teste.produtoapi.api.repository.filter.ProdutoFilter;
import com.teste.produtoapi.domain.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@GetMapping
	public Page<Produto> listar(ProdutoFilter produtoFilter, Pageable pageable){
		return produtoRepository.filtrar(produtoFilter, pageable);
	}
	
	@GetMapping("/{produtoId}")
	public Produto buscar(@PathVariable String produtoId) {
		return produtoService.buscarOuFalhar(produtoId);
	}
	
	@PostMapping
	public ResponseEntity<Produto> salvar(@Valid @RequestBody Produto produto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.salvar(produto));
	}
	
	@PatchMapping("/{produtoId}")
	public ResponseEntity<Produto> atualizar(@PathVariable String produtoId, @Valid @RequestBody Produto produto) {
		Produto produtoSalvo = produtoService.buscarOuFalhar(produtoId);
		
		BeanUtils.copyProperties(produto, produtoSalvo, "id");
		
		produtoService.salvar(produtoSalvo);
		return ResponseEntity.ok(produtoSalvo);
	}
	
	@DeleteMapping("/{produtoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable String produtoId) {
		produtoService.remover(produtoId);
	}

}
