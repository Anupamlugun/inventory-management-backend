package com.micro.SupplierStock.controller;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.micro.SupplierStock.model.Supplier;

import com.micro.SupplierStock.service.SupplierService;

@RestController
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @PostMapping("/savesupplier")
    public String saveSup(@RequestBody Supplier supplier) {
        return supplierService.saveSupplier(supplier);

    }

    // get supplier by Id
    @GetMapping("/getsupplierbyid/{supplierId}")
    public Optional<Supplier> getSupplierById(@PathVariable Long supplierId) {
        return supplierService.getSupplierById(supplierId);
    }

    @GetMapping("/getsupplier")
    public List<Supplier> getSup() {
        return supplierService.getSupplier();
    }

    @PutMapping("/updatesupplier/{supplier_id}")
    public String updateSup(@PathVariable Long supplier_id, @RequestBody Supplier supplier) {
        return supplierService.updateSupplier(supplier_id, supplier);
    }

    @PutMapping("/deletesupplier/{supplier_id}")
    public String deleteSup(@PathVariable Long supplier_id) {
        return supplierService.deleteSupplier(supplier_id);
    }

}
