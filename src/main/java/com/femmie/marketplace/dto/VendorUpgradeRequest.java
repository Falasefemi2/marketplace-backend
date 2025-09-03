package com.femmie.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorUpgradeRequest {
    @NotBlank(message = "Shop name is required")
    private String shopName;

    @NotBlank(message = "Business address is required")
    private String businessAddress;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}