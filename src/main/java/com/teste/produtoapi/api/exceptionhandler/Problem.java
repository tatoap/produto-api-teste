package com.teste.produtoapi.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class Problem {
	
	private Integer status;
	
	private OffsetDateTime timestamp;
	
	private String tipo;
	
	private String titulo;
	
	private String detalhe;
	
	private String mensagemUsuario;
	
	private List<Object> objects;
	
	@Getter
	@Builder
	public static class Object {
		
		private String nome;
		
		private String mensagemUsuario;
		
	}

}
