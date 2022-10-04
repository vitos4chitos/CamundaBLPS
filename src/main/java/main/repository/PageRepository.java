package main.repository;

import main.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

  Optional<Page> getPageById(Long pageId);

  Optional<Page> getPageByName(String name);

  Boolean existsPageByName(String pageName);
}
