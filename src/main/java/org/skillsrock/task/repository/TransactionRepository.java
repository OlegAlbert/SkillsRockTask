package org.skillsrock.task.repository;

import java.util.UUID;
import org.skillsrock.task.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}
