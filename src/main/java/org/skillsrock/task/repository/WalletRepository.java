package org.skillsrock.task.repository;

import java.util.UUID;
import org.skillsrock.task.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

}
