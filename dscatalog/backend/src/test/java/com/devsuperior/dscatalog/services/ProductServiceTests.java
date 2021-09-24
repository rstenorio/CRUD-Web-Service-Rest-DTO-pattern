package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.DTO.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.repositories.factories.Factory;
import com.devsuperior.dscatalog.services.exceptions.DBException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	long idExists;
	long idNonExists;
	long idDependent;
	long countTotalProduct;
	
	private PageImpl<Product> page;
	
	private Product prod;
	
	private Category category;
	
	private ProductDTO prodDTO;
	
	
	@BeforeEach
	void setUp() throws Exception {
		idExists = 1L;
		idNonExists = 1000L;
		countTotalProduct = 25L;
		idDependent = 4L;
		prod = Factory.createProduct();
		prodDTO = Factory.createProductDTO();
		category = Factory.createCategory();
		
		page = new PageImpl<>(List.of(prod));
		
		//Configuration @mock(repository)
		
		//esempi giu ci serve per metodo che ritorna valore
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(prod);
		Mockito.when(repository.findById(idExists)).thenReturn(Optional.of(prod));
		Mockito.when(repository.findById(idNonExists)).thenReturn(Optional.empty());

		//update
		Mockito.when(repository.getOne(idExists)).thenReturn(prod);
		Mockito.when(repository.getOne(idNonExists)).thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRepository.getOne(idExists)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(idNonExists)).thenThrow(EntityNotFoundException.class);

		
		//esempi giu ci serve solamente per metodo void()
		Mockito.doNothing().when(repository).deleteById(idExists);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(idNonExists);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependent);
	}
	
	@Test
	public void updateShouldReturnEntityWhenIdExists() {
		ProductDTO prod = service.update(prodDTO,idExists);
		Assertions.assertNotNull(prod);
		
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdNonExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(prodDTO, idNonExists);
		});

	}
	
	@Test
	public void findByIdShouldReturnEntityWhenIdExists() {
		ProductDTO prod = service.findById(idExists);
		Assertions.assertNotNull(prod);
		
		Mockito.verify(repository,Mockito.times(1)).findById(idExists);
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNonExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			service.findById(idNonExists);	
		});
		
		Mockito.verify(repository,Mockito.times(1)).findById(idNonExists);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> res = service.findAllPaged(pageable);
		Assertions.assertNotNull(res);
		Mockito.verify(repository,Mockito.times(1)).findAll(pageable);
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenIdNonExists() {
		Assertions.assertThrows(DBException.class ,() ->{
			service.delete(idDependent);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idDependent);
	}
	
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdNonExists() {
		Assertions.assertThrows(ResourceNotFoundException.class ,() ->{
			service.delete(idNonExists);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idNonExists);
	}

	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() ->{
			service.delete(idExists);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idExists);
	}
	
	
}
