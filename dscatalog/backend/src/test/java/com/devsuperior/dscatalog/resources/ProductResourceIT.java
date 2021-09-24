package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.DTO.ProductDTO;
import com.devsuperior.dscatalog.repositories.factories.Factory;
import com.devsuperior.dscatalog.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {
	@Autowired
	private ProductResource resource;

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ProductService service;

	@Autowired
	private ObjectMapper OM;

	
	private Long id;
	private Long nonId;
	private Long totalProduct;

	@BeforeEach
	void SetUp() throws Exception {
		id = 1L;
		nonId = 1000L;
		totalProduct = 25L;
	}

	
	@Test
	public void updateShouldReturnEntityWhenIdExists() throws Exception{
		ProductDTO dto = Factory.createProductDTO();

		//transforma uma entity em String
		String jsonBody = OM.writeValueAsString(dto);
		
		ResultActions res = mockMvc
				.perform(put("/products/{id}",id)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		res.andExpect(jsonPath("$.id").exists());
		res.andExpect(jsonPath("$.name").value(dto.getName()));
		res.andExpect(jsonPath("$.description").value(dto.getDescription()));

	}	

	
	@Test
	public void updateShouldThrowNotFounWhenIdNonExists() throws Exception{
		ProductDTO dto = Factory.createProductDTO();

		//transforma uma entity em String
		String jsonBody = OM.writeValueAsString(dto);
		
		ResultActions res = mockMvc
				.perform(put("/products/{id}",nonId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		res.andExpect(status().isNotFound());
	}
		
	@Test
	public void findAllShouldReturnSortedWhenSortByName() throws Exception {
		ResultActions res = mockMvc
				.perform(get("/products?page=0&size=12&sort=name,asc")
				.accept(MediaType.APPLICATION_JSON));

		res.andExpect(status().isOk());
		res.andExpect(jsonPath("$.totalElements").value(totalProduct));
		res.andExpect(jsonPath("$.content").exists());
		res.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		res.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		res.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
}
