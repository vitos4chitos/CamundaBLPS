package main.repository;

import main.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

  List<Author> findAuthorsByPageId(Long pageId);
}
