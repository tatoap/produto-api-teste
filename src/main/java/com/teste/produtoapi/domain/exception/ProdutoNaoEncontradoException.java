package com.teste.produtoapi.domain.exception;

public class ProdutoNaoEncontradoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	private static final String MSG_PRODUTO_NAO_ENCONTRADO = "Não existe um cadastro de produto com o código '%s'";

	public ProdutoNaoEncontradoException(String produtoId) {
		super(String.format(MSG_PRODUTO_NAO_ENCONTRADO, produtoId));
	}
	
}
