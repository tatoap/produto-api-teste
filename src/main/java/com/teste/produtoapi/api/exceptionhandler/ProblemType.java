package com.teste.produtoapi.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemType {
	
	MENSAGEM_INCOMPREENSIVEL("/mensagem-incompreensivel", "Mensagem incompreensivel"),
	RECURSO_NAO_ENCONTRADO("/recurso-nao-encontrado", "Recurso não encontrado"),
	ERRO_NEGOCIO("/erro-negocio", "Violação de regra de negócio"),
	DADOS_INVALIDOS("/dados-invalidos", "Dados inválidos"),
	PARAMETRO_INVALIDO("/parametro-invalido", "Parâmetro inválido"),
	ERRO_DE_SISTEMA("/erro-de-sistema", "Erro de sistema"),;
	
	private String titulo;
	private String uri;
	
	private ProblemType(String path, String titulo) {
		this.titulo = titulo;
		this.uri = "http://produto-api.com.br" + path;
	}

}
