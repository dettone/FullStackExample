package com.example.algamoney.resource;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.event.RecursoCriadoEvent;
import com.example.algamoney.exceptionhandler.AlgaMoneyExceptionHandler.Erro;
import com.example.algamoney.model.Lancamento;
import com.example.algamoney.repository.LancamentoRepository;
import com.example.algamoney.repository.filter.LancamentoFilter;
import com.example.algamoney.repository.projection.ResumoLancamento;
import com.example.algamoney.service.LancamentoService;
import com.example.algamoney.service.exception.PessoaInativaException;

@RestController
@RequestMapping("/lancamento")
public class LancamentoResource {
	@Autowired
	LancamentoRepository lancamentoRepository;

	// aplicador de aplication event
	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}
	
	@GetMapping(params = "resumo") //se tiver esse parametro ele chama esse metodo
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}


	@SuppressWarnings("rawtypes")
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO')")

	public ResponseEntity buscarPeloCodigo(@PathVariable Long codigo) {
		return lancamentoRepository.findById(codigo).map(lancamento -> ResponseEntity.ok(lancamento))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")

	public ResponseEntity<Lancamento> criar(@RequestBody Lancamento lancamento, HttpServletResponse response) {
		lancamentoService.salvar(lancamento);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamento.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamento);
	}

	// exception unica
	@ExceptionHandler({ PessoaInativaException.class })
	public ResponseEntity<Object> handlePessoaInativaException(PessoaInativaException ex) {
		String mensagemUsuario = messageSource.getMessage("pessoa.inativa", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo) {
		this.lancamentoRepository.deleteById(codigo);
		/*
		 * ouuu Pessoa p = new Pessoa(); pessoa.setCodigo(codigo);
		 * this.pessoaRepository.delete(p);
		 */
	}

}
