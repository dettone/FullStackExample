package com.example.algamoney.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.model.Categoria;
import com.example.algamoney.repository.CategoriaRepository;

@Service
public class CategoriaService {
	@Autowired
	CategoriaRepository categoriaRepository;

	public Categoria editar(Long codigo, Categoria categoria) {
		Categoria categoriaSalva = extracted(codigo);
		BeanUtils.copyProperties(categoria, categoriaSalva);
		return categoriaRepository.save(categoriaSalva);
	}

	private Categoria extracted(Long codigo) {
		Categoria categoriaSalva = this.categoriaRepository.findById(codigo)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return categoriaSalva;
	}

}
