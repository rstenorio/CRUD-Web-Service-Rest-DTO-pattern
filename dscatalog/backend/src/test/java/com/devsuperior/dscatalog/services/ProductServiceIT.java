package com.devsuperior.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.criterion.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.DTO.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional //rollback no BD, garante os testes como estado original
public class ProductServiceIT {
	@Autowired
	private ProductService service;
	
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
	public void deleteShouldDoItWhenIdExisting() {
		service.delete(idExists);
		assertEquals(countTotalProduct - 1 , repository.count());
	}
	
	@Test
	public void deleteShouldThrowExceptionNotFoundWhenIdNonExisting() {
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			service.delete(idNonExists);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPagWhenPage0Size10OrderedByName() {
		PageRequest page = PageRequest.of(0, 10, Sort.by("name"));
		Page<ProductDTO> res = service.findAllPaged(page);
		Assertions.assertFalse(res.isEmpty());
		assertEquals(countTotalProduct, res.getTotalElements());
		assertEquals(0, res.getNumber());
		assertEquals(10,res.getSize());
		assertEquals("Macbook Pro", res.getContent().get(0).getName());
		assertEquals("PC Gamer", res.getContent().get(1).getName());
		assertEquals("PC Gamer Alfa", res.getContent().get(2).getName());

		//service.findAllPaged(page);
		//Assertions.assertTrue(repository.count() > 0);		
	}
	
	
	
}
