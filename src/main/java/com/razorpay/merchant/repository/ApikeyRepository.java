package com.razorpay.merchant.repository;

import com.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApikeyRepository extends JpaRepository<ApiKey, UUID> {
}
