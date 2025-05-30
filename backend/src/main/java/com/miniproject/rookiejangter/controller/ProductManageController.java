package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.ProductDTO;
import com.miniproject.rookiejangter.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class ProductManageController {
    private final ProductService productService;


}
