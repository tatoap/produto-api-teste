package com.teste.produtoapi.api.repository.filter;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoFilter {
	
	private String nome;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataCriacaoDe;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataCriacaoAte;

}
