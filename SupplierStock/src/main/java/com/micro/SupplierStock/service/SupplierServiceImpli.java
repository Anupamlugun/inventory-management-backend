package com.micro.SupplierStock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.micro.SupplierStock.entity.SupplierEntity;
import com.micro.SupplierStock.model.Supplier;
import com.micro.SupplierStock.repository.SupplierRepository;

@Service
public class SupplierServiceImpli implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public String saveSupplier(Supplier supplier) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "User is not authenticated!";
        }

        String email = authentication.getName();

        SupplierEntity supplierEntity = new SupplierEntity();

        supplierEntity.setSupplierId(supplier.getSupplier_id());
        supplierEntity.setSupplierName(supplier.getSupplier_name());
        supplierEntity.setSupplierPhone(supplier.getSupplier_phone());
        supplierEntity.setSupplierEmail(supplier.getSupplier_email());
        supplierEntity.setSupplierAddress(supplier.getSupplier_address());

        supplierEntity.setEmail(email);
        supplierEntity.setStatus(true);

        List<SupplierEntity> supplierEntities = supplierRepository.findAllByEmail(email);
        for (SupplierEntity supplierEntity2 : supplierEntities) {
            if ((supplier.getSupplier_phone().equals(supplierEntity2.getSupplierPhone()) ||
                    supplier.getSupplier_email().equals(supplierEntity2.getSupplierEmail())) &&
                    supplierEntity2.getStatus()) {
                return "Your phone number or email ID already exists";
            } else if ((supplier.getSupplier_phone().equals(supplierEntity2.getSupplierPhone()) ||
                    supplier.getSupplier_email().equals(supplierEntity2.getSupplierEmail())) &&
                    !supplierEntity2.getStatus()) {

                supplierEntity2.setSupplierName(supplier.getSupplier_name());
                supplierEntity2.setSupplierPhone(supplier.getSupplier_phone());
                supplierEntity2.setSupplierEmail(supplier.getSupplier_email());
                supplierEntity2.setSupplierAddress(supplier.getSupplier_address());
                supplierEntity2.setStatus(true);
                supplierRepository.save(supplierEntity2);
                return "Supplier Saved";
            }
        }

        supplierRepository.save(supplierEntity);
        return "Supplier Saved";
    }

    @Override
    public List<Supplier> getSupplier() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or the user is not authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            // return "User is not authenticated!";
        }

        String email = authentication.getName();

        List<SupplierEntity> supplierEntities = supplierRepository.findByStatusTrueAndEmail(email);
        List<Supplier> suppliers = new ArrayList<>();
        for (SupplierEntity supplierEntity : supplierEntities) {
            Supplier supplier = new Supplier();
            supplier.setSupplier_id(supplierEntity.getSupplierId());
            supplier.setSupplier_name(supplierEntity.getSupplierName());
            supplier.setSupplier_phone(supplierEntity.getSupplierPhone());
            supplier.setSupplier_email(supplierEntity.getSupplierEmail());
            supplier.setSupplier_address(supplierEntity.getSupplierAddress());

            suppliers.add(supplier);
        }
        return suppliers;
    }

    // get supplier by id
    @Override
    public Optional<Supplier> getSupplierById(Long supplierId) {
        return supplierRepository.findAll().stream()
                .filter(supplierEntity -> supplierEntity.getSupplierId().equals(supplierId))
                .findFirst()
                .map(supplierEntity -> {
                    Supplier supplier = new Supplier();
                    supplier.setSupplier_id(supplierEntity.getSupplierId());
                    supplier.setSupplier_name(supplierEntity.getSupplierName());
                    supplier.setSupplier_phone(supplierEntity.getSupplierPhone());
                    supplier.setSupplier_email(supplierEntity.getSupplierEmail());
                    supplier.setSupplier_address(supplierEntity.getSupplierAddress());
                    return supplier;
                });
    }

    @Override
    public String updateSupplier(Long supplier_id, Supplier supplier) {

        List<SupplierEntity> supplierEntities = supplierRepository.findAll();
        for (SupplierEntity supplierEntity2 : supplierEntities) {
            if ((supplier.getSupplier_phone().equals(supplierEntity2.getSupplierPhone()) ||
                    supplier.getSupplier_email().equals(supplierEntity2.getSupplierEmail())) &&
                    supplierEntity2.getStatus()) {
                return "Your phone number or email ID already exists";
            } else if ((supplier.getSupplier_phone().equals(supplierEntity2.getSupplierPhone()) ||
                    supplier.getSupplier_email().equals(supplierEntity2.getSupplierEmail())) &&
                    !supplierEntity2.getStatus()) {

                supplierEntity2.setSupplierName(supplier.getSupplier_name());
                supplierEntity2.setSupplierPhone(supplier.getSupplier_phone());
                supplierEntity2.setSupplierEmail(supplier.getSupplier_email());
                supplierEntity2.setSupplierAddress(supplier.getSupplier_address());
                supplierEntity2.setStatus(true);
                supplierRepository.save(supplierEntity2);
                return "Supplier updated successfully";
            }
        }

        if (supplierRepository.existsById(supplier_id)) {
            SupplierEntity supplierEntity = new SupplierEntity();
            supplierEntity.setSupplierId(supplier_id);
            supplierEntity.setSupplierName(supplier.getSupplier_name());
            supplierEntity.setSupplierPhone(supplier.getSupplier_phone());
            supplierEntity.setSupplierEmail(supplier.getSupplier_email());
            supplierEntity.setSupplierAddress(supplier.getSupplier_address());

            supplierRepository.save(supplierEntity);
            return "Supplier updated successfully";
        }
        return "Supplier not found";
    }

    @Override
    public String deleteSupplier(Long supplier_id) {
        if (!supplierRepository.existsById(supplier_id)) {
            return "Supplier not found";
        }

        supplierRepository.updateSupplierStatus(supplier_id, false);
        return "Supplier deleted successfully";
    }
}
