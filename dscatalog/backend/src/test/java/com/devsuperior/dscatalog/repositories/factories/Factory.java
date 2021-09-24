package com.devsuperior.dscatalog.repositories.factories;

import java.time.Instant;

import com.devsuperior.dscatalog.DTO.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		
		Product prod = new Product(1L, "Phone", "Xiaomi", 198.0, "https://img.com/img.png", Instant.parse("2021-09-21T03:00:00Z"));
		prod.getCategories().add(createCategory());
		return prod;
	}
	
	public static ProductDTO createProductDTO() {
		Product prod = createProduct();
		return new ProductDTO(prod,prod.getCategories());
	}
	
	public static Category createCategory() {
		return new Category(2L,"Electronics");
	}
	
	
}
