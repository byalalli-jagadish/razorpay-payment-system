package com.razorpay.merchant.repository;

import com.razorpay.merchant.entity.AppUser;
import com.razorpay.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppUSerRepository extends JpaRepository<AppUser, UUID> {
}
