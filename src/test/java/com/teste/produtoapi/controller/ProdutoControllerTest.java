package com.teste.produtoapi.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.produtoapi.api.model.Produto;
import com.teste.produtoapi.api.repository.ProdutoRepository;
import com.teste.produtoapi.domain.exception.EntidadeNaoEncontradaException;
import com.teste.produtoapi.domain.service.ProdutoService;

@SpringBootTest
@AutoConfigureMockMvc
public class ProdutoControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private Produto produto;
	
	@MockBean
	private ProdutoRepository produtoRepository;
	
	@MockBean
	private ProdutoService produtoService;
		
	@BeforeEach
	public void setup() throws JsonProcessingException {
		
		produto = new Produto("123456teste", "TV Smart", new BigDecimal(2000), new BigDecimal(300), 1, LocalDate.of(2021, 06, 10));
		
	}
	
	@Test
	public void deveRetornar200_QuandoConsultarProdutos() throws Exception {
		
		mockMvc.perform(get("/produtos"))
			.andExpect(status().isOk());
			
	}
	
	@Test
	public void deveRetornar201_QuandoCadastrarProduto() throws JsonProcessingException, Exception {
				
		mockMvc.perform(post("/produtos")
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectMapper.writeValueAsString(produto)))
			.andExpect(status().isCreated());
		
	}
	
	@Test
	public void deveRetornar200_QuandoConsultarProduto() throws Exception {
		
		when(produtoService.buscarOuFalhar(produto.getId())).thenReturn(produto);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/produtos/{produtoId}", produto.getId()))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(produto.getId())));
		
		verify(produtoService, times(1)).buscarOuFalhar(produto.getId());
			
	}
	
	@Test
	public void deveRetornar200_QuandoAtualizarProduto() throws Exception {
		
		when(produtoService.buscarOuFalhar(produto.getId())).thenReturn(produto);
		when(produtoService.salvar(produto)).thenReturn(produto);
		
		mockMvc.perform(MockMvcRequestBuilders.patch("/produtos/{produtoId}", produto.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(produto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(produto.getId())));
		
		verify(produtoService, times(1)).buscarOuFalhar(produto.getId());
		verify(produtoService, times(1)).salvar(produto);

	}
	
	@Test
	public void deveRetornar204_QuandoExcluirUmProduto() throws Exception {
		
		when(produtoService.buscarOuFalhar(produto.getId())).thenReturn(produto);
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/produtos/{produtoId}", produto.getId()))
				.andExpect(status().isNoContent());
		
		verify(produtoService, times(1)).remover(produto.getId());
	}
	
	@Test
	public void deveRetornar404_QuandoConsultarProdutoIdIncorreto() throws Exception {
		
		String produtoErradoId = "idErrado";
		
		when(produtoService.buscarOuFalhar(produtoErradoId)).thenThrow(new EntidadeNaoEncontradaException(produtoErradoId));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/produtos/{produtoId}", produtoErradoId))
			.andExpect(status().isNotFound());
		
		verify(produtoService, times(1)).buscarOuFalhar(produtoErradoId);
			
	}
	
}
