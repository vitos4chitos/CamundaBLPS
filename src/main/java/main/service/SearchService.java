package main.service;

import main.entity.Page;
import main.repository.SearchRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SearchService {

  private final SearchRepository searchRepository;
  private final RecommendationService recommendationService;

  public SearchService(SearchRepository pageRepository, RecommendationService recommendationService) {
    this.searchRepository = pageRepository;
    this.recommendationService = recommendationService;
  }

  public Page getPageByName(String name) {
    Optional<Page> page = searchRepository.getPageByName(name);
    return page.orElse(null);
  }

//  public Page getPageById(Long id) {
//    return searchRepository.getPageById(id);
//  }

//  public List<Page> getAll() {
//    return searchRepository.getAll();
//  }

  public String getAnswer(String name){
      Page page = getPageByName(name);
      if(page == null){
        return null;
      }
      return page.getText();
  }
}
