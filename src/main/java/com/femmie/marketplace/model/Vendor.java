package com.femmie.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    private String businessAddress;
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
