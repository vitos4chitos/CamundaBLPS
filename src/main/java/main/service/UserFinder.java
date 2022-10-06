package main.service;

import java.util.List;

public interface UserFinder {
  List<Long> findAllCoAuthorUsers(Long pageId);
}
