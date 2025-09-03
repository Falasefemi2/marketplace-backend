package com.femmie.marketplace.repository;

import com.femmie.marketplace.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Optional<Vendor> findByUser_Email(String email);
}