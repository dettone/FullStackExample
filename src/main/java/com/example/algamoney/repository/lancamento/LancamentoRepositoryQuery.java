package com.example.algamoney.repository.lancamento;
	
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.algamoney.model.Lancamento;
import com.example.algamoney.repository.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
}
