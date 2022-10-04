package main.repository;

import main.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
public interface SearchRepository extends JpaRepository<Page, Long> {

  Page getPageById(Long id);

  Optional<Page> getPageByName(String name);

  Boolean existsPageByName(String name);

  @Transactional
  @Modifying
  @Query("update pages p set p.text = ?1 where p.id = ?2")
  void setUserInfoById(String text, Long id);
}
