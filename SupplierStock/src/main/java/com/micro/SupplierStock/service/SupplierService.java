package com.micro.SupplierStock.service;

import java.util.List;
import java.util.Optional;

import com.micro.SupplierStock.model.Supplier;

public interface SupplierService {

    public String saveSupplier(Supplier supplier);

    public List<Supplier> getSupplier();

    Optional<Supplier> getSupplierById(Long supplierId);

    public String updateSupplier(Long supplier_id, Supplier supplier);

    public String deleteSupplier(Long supplier_id);
}
