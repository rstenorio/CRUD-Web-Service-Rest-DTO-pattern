package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.DTO.ProductDTO;
import com.devsuperior.dscatalog.repositories.factories.Factory;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService service;

	@Autowired
	private ObjectMapper OM;
	
	private Long id;
	private Long nonId;
	private Long integratedId;
	
	private ProductDTO dto;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		id = 1L;
		nonId = 2L;
		dto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(dto));
		
		Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(dto);
		
		
		Mockito.when(service.update(ArgumentMatchers.any(), ArgumentMatchers.eq(id))).thenReturn(dto);
		Mockito.when(service.update(ArgumentMatchers.any(), ArgumentMatchers.eq(nonId))).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(id)).thenReturn(dto);
		Mockito.when(service.findById(nonId)).thenThrow(ResourceNotFoundException.class);
		
		//metodo void
		Mockito.doNothing().when(service).delete(id);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(service).delete(integratedId);
	}
	
	
	@Test
	public void insertShouldDoIt() throws Exception {
		String jsonBody = OM.writeValueAsString(dto);
		
		ResultActions res = mockMvc
				.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		res.andExpect(status().isCreated());
		
		res.andExpect(jsonPath("$.id").exists());
		res.andExpect(jsonPath("$.name").exists());
		res.andExpect(jsonPath("$.description").exists());
	}
	
	
	
	@Test
	public void deleteShouldDoItWhenIdExists() throws Exception {
		ResultActions res = mockMvc
				.perform(delete("/products/{id}",id)
				.accept(MediaType.APPLICATION_JSON));
		
		res.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdNonExists() throws Exception {
		ResultActions res = mockMvc
				.perform(delete("/products/{id}",nonId)
				.accept(MediaType.APPLICATION_JSON));
		
		res.andExpect(status().isNotFound());
		
	}

	@Test
	public void updateShouldReturnEntityWhenIdExists() throws Exception{
	
		//transforma uma entity em String
		String jsonBody = OM.writeValueAsString(dto);
		
		ResultActions res = mockMvc
				.perform(put("/products/{id}",id)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		res.andExpect(jsonPath("$.id").exists());
		res.andExpect(jsonPath("$.name").exists());
		res.andExpect(jsonPath("$.description").exists());

	}
	
	@Test
	public void updateShouldThrowNotFoundWhenIdNonExists() throws Exception {
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
	public void findByIdShouldReturnEntityWhenIdExists() throws Exception {
		ResultActions res = mockMvc
				.perform(get("/products/{id}",id)
				.accept(MediaType.APPLICATION_JSON));
		res.andExpect(status().isOk());
		res.andExpect(jsonPath("$.id").exists());
		res.andExpect(jsonPath("$.name").exists());
		res.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNonExists() throws Exception {
		ResultActions res = mockMvc
				.perform(get("/products/{id}",nonId)
				.accept(MediaType.APPLICATION_JSON));
		res.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions res = mockMvc
				.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON));
		
		res.andExpect(status().isOk());
	}
	
	
}
