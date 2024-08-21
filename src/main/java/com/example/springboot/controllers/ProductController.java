package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static  org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static  org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;




@RestController
public class ProductController {

    //injeção de dependencia via @Autowired
    @Autowired
    ProductRepository productRepository;


    //* getAll e getOne de produtos */
    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> productList = productRepository.findAll();

        if (!productList.isEmpty()) {
            for(ProductModel product : productList){
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }
      
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> prodOptional = productRepository.findById(id);

        if (prodOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(prodOptional.get());
    }
    

    //*Post de um produto */
    @PostMapping("/products")
    public ResponseEntity<ProductModel> postProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        var productModel = new ProductModel();

        BeanUtils.copyProperties(productRecordDto, productModel);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }
    
    //*Put de um produto */
    @PutMapping("products/{id}")
    public ResponseEntity<Object> putProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto) {
        Optional<ProductModel> prodOptional = productRepository.findById(id);

        if (prodOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        var productModel = prodOptional.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    //*Delete de um produto */
    @DeleteMapping("products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> prodOptional = productRepository.findById(id);

        if (prodOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        productRepository.delete(prodOptional.get());

        return  ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }
}
