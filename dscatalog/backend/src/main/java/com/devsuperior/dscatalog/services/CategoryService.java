package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.DTO.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DBException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true) //evita locking no BD
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){
		
		Page<Category> page = repository.findAll(pageRequest);
		return page.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true) //evita locking no BD
	public CategoryDTO findById(Long id){
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id non trovato"));
		
		return new CategoryDTO(entity);
		
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category cat = new Category();
		cat.setName(dto.getName());
		cat =  repository.save(cat);
		return new CategoryDTO(cat);
	}

	@Transactional
	public CategoryDTO update(CategoryDTO dto, Long id) {
		try {
			Category cat = repository.getOne(id); // reserva objeto
			cat.setName(dto.getName());
			cat = repository.save(cat);
			return new CategoryDTO(cat);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id non Trovato");
		}
	}

	//caso coloque transactional nao sera possivel capturar exception
	public void delete(Long id) {
		try {
			repository.deleteById(id);		
		}catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id non trovato: " + id); 
		}catch (DataIntegrityViolationException  e) {
			throw new DBException("Integrity Violation");
		}
		
	}


}
