package main.repository;

import main.entity.Check;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CheckRepository extends JpaRepository<Check, Long> {
    List<Check> getAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update check c set c.is_confirmed = ?1, c.comment = ?2 where c.id = ?3")
    void setUserInfoById(Boolean flag, String comment, Long id);

    List<Check> getAllByChangeId(Long changeId);

    @Transactional
    void deleteAllByChangeId(Long id);
}
