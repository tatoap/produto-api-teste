package com.teste.produtoapi.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Produto {
	
	@Id
	@EqualsAndHashCode.Include
	private String id;
	
	@NotBlank
	private String nome;
	
	@NotNull
	@PositiveOrZero
	private BigDecimal valorReal;
	
	@NotNull
	@PositiveOrZero
	private BigDecimal valorDolar;
	
	@NotNull
	@PositiveOrZero
	private Integer quantidade;
	
	@NotNull
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataCriacao;
	
	public Produto() {
		
	}

	public Produto(String nome, BigDecimal valorReal,
			BigDecimal valorDolar, int quantidade, LocalDate dataCriacao) {
		this.nome = nome;
		this.valorReal = valorReal;
		this.valorDolar = valorDolar;
		this.quantidade = quantidade;
		this.dataCriacao = dataCriacao;
	}
	
	public Produto(String id, String nome, BigDecimal valorReal,
			BigDecimal valorDolar, int quantidade, LocalDate dataCriacao) {
		this.id = id;
		this.nome = nome;
		this.valorReal = valorReal;
		this.valorDolar = valorDolar;
		this.quantidade = quantidade;
		this.dataCriacao = dataCriacao;
	}
	
}
