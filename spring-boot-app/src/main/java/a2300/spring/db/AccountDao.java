package a2300.spring.db;

import a2300.spring.db.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountDao extends JpaRepository<Account, Long> {
    @Override
    List<Account> findAll();

    @Override
    Optional<Account> findById(Long id);

    @Override
    <S extends Account> S save(S entity);
}
