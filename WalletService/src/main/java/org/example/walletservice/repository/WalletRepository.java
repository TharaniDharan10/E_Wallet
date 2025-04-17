package org.example.walletservice.repository;

import jakarta.transaction.Transactional;
import org.example.walletservice.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByPhoneNo(String phoneNo);

    @Transactional  //it is good practice to annotate query which is modifying data with @Transactional
    @Modifying  //whenever we make an update in db while executing a query
    @Query("update Wallet w set w.balance = w.balance+:amount where w.phoneNo = :phoneNo")  //put : before any value taken from method parameters
    void updateWallet(String phoneNo, Double amount);
}
