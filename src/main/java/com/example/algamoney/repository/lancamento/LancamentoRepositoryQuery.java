package com.example.algamoney.repository.lancamento;

import java.util.List;

import com.example.algamoney.model.Lancamento;
import com.example.algamoney.repository.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {
	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter);
}
