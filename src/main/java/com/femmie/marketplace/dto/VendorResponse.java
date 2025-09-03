package com.femmie.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponse {
    private Long id;
    private String shopName;
    private String businessAddress;
    private String phoneNumber;
}