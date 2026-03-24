package com.btg.funds.repository;

import com.btg.funds.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByClientIdAndFundIdAndActiveTrue(Long clientId, Long fundId);

    List<Subscription> findByClientIdAndActiveTrue(Long clientId);
}
