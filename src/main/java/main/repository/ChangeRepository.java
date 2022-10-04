package main.repository;

import main.entity.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Component
public interface ChangeRepository extends JpaRepository<Change, Long> {
    Optional<Change> getChangeByPageId(Long pageId);

    List<Change> getAllByPageId(Long id);

    Optional<Change> getChangeById(Long id);

    Boolean existsByPageId(Long pageId);

    @Transactional
    @Modifying
    @Query("update change c set c.is_confirmed = ?1 where c.id = ?2")
    void setUserInfoById(Boolean flag, Long id);

    @Transactional
    void deleteByPageId(Long id);
}

