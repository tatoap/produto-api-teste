package com.teste.produtoapi.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.flywaydb.core.internal.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.teste.produtoapi.domain.exception.EntidadeNaoEncontradaException;
import com.teste.produtoapi.domain.exception.NegocioException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final String MSG_REQUISICAO_INVALIDA = "O corpo da requisição esta inválido, verifique erro de sintaxe.";
	
	private static final String MSG_RECURSO_NAO_ENCONTRADO = "Não existe o recurso '%s' que você tentou acessar";

	private static final String MSG_CAMPOS_INVALIDOS = "Um ou mais campos estão inválidos, faça o preenchimento correto e tente novamente";
	
	private static final String MSG_PROPRIEDADE_NAO_EXISTE = "A propriedade '%s' não existe. Por favor, verifique "
			+ "a requisição enviada e tente novamente.";
	
	public static final String MSG_ERRO_GENERICA_USUARIO_FINAL = 
			"Ocorreu um erro interno inesperado no sistema, tente novamente e se o "
			+ "problema persistir, entre em contato com o administrador do sistema.";
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Throwable rootCause = ExceptionUtils.getRootCause(ex);
		
		if (rootCause instanceof NoHandlerFoundException) {
			return handleNoHandlerFoundException((NoHandlerFoundException) rootCause, headers, status, request);
		}
		
		if (rootCause instanceof PropertyBindingException) {
			return handlePropertyBinding((PropertyBindingException) rootCause, headers, status, request);
		}
		
		if (rootCause instanceof MethodArgumentNotValidException) {
			return handleMethodArgumentNotValid((MethodArgumentNotValidException) rootCause, headers, status, request);
		}
		
		ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
		
		String detail = MSG_REQUISICAO_INVALIDA;
		
		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();
		
		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex, WebRequest request) {
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		
		ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
		
		String detail = ex.getMessage();
		
		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();
		
		return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	private ResponseEntity<Object> handlePropertyBinding(PropertyBindingException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
		
		String path = joinPath(ex.getPath());
		
		String detail = String.format(MSG_PROPRIEDADE_NAO_EXISTE, path);
		
		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();
		
		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	private ResponseEntity<Object> handleValidationInternal(Exception ex, BindingResult bindingResult, HttpHeaders headers,
			HttpStatus status, WebRequest request){
		
		ProblemType problemType = ProblemType.DADOS_INVALIDOS;
		
		String detail = MSG_CAMPOS_INVALIDOS;
		
		List<Problem.Object> problemObjects = bindingResult.getAllErrors().stream()
				.map(objectError -> {
					String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
					
					String name = objectError.getObjectName();
					
					if (objectError instanceof FieldError) {
						name = ((FieldError) objectError).getField();
					}
					
					return Problem.Object.builder()
							.nome(name)
							.mensagemUsuario(message)
							.build();
				})
				.collect(Collectors.toList());
		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.objects(problemObjects)
				.build();
		
		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		return handleValidationInternal(ex, ex.getBindingResult(), new HttpHeaders(), status, request);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
		
		String detail = String.format(MSG_RECURSO_NAO_ENCONTRADO, ex.getRequestURL());
		
		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();
		
		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<?> handleNegocio(NegocioException ex, WebRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		ProblemType problemType = ProblemType.ERRO_NEGOCIO;
		
		String detail = ex.getMessage();
		
		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();
		
		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleUncaught(Exception ex, WebRequest request){
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		
		ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
		
		String detail = MSG_ERRO_GENERICA_USUARIO_FINAL;
		
		log.error(ex.getMessage(), ex);
		
		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();
		
		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		if (body == null) {
			body = Problem.builder()
					.timestamp(OffsetDateTime.now())
					.titulo(status.getReasonPhrase())
					.mensagemUsuario(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.build();
		} else if (body instanceof String) {
			body = Problem.builder()
					.timestamp(OffsetDateTime.now())
					.titulo((String) body)
					.status(status.value())
					.mensagemUsuario(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.build();
		}
		
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	private Problem.ProblemBuilder createProblemBuilder(HttpStatus status, 
 			ProblemType problemType, String detail) {
		
		return Problem.builder()
				.timestamp(OffsetDateTime.now())
				.status(status.value())
				.tipo(problemType.getUri())
				.titulo(problemType.getTitulo())
				.detalhe(detail);
	}
	
	private String joinPath(List<Reference> reference) {
		return reference.stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));
	}

}
