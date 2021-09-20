package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.factories.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	@Autowired
	private ProductRepository repository;

	long idExists;
	long idNonExists;
	long countTotalProduct;
	
	@BeforeEach
	void setUp() throws Exception {
		idExists = 1L;
		idNonExists = 1000L;
		countTotalProduct = 25L;
	}
	
	
	@Test
	public void deleteWhenIdExists() {
		repository.deleteById(idExists);
		Optional<Product> res = repository.findById(idExists);

		Assertions.assertFalse(res.isPresent());

	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdNoExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(idNonExists);
		});
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Product prod = Factory.createProduct();
		prod.setId(null);
		prod = repository.save(prod);
		
		Assertions.assertNotNull(prod.getId());
		Assertions.assertEquals(countTotalProduct + 1, prod.getId());
		
	}
	
	@Test
	public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
		Optional<Product> prod = repository.findById(idExists);
		Assertions.assertFalse(prod.isEmpty());
	}

	
	
	@Test
	public void findByIdShouldReturnEmptyOptionalWhenIdNonExists() {
		Optional<Product> prod =  repository.findById(idNonExists);
		Assertions.assertTrue(prod.isEmpty());
	}

}
