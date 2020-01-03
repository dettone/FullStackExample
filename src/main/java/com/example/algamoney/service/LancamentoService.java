package com.example.algamoney.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.model.Lancamento;
import com.example.algamoney.model.Pessoa;
import com.example.algamoney.repository.LancamentoRepository;
import com.example.algamoney.repository.PessoaRepository;
import com.example.algamoney.service.exception.PessoaInativaException;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private	LancamentoRepository lancamentoRepository;

	public Lancamento salvar(Lancamento lancamento) {
		if (lancamento == null || lancamento.getPessoa() == null || lancamento.getPessoa().getCodigo() == null) {
			throw new IllegalArgumentException("É necessário informar o código da pessoa para criar um lançamento.");
		}
	
		Pessoa pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo())
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		if(!pessoa.isAtivo()) {
			throw new PessoaInativaException();
		}
		return lancamentoRepository.save(lancamento);

	}

}
