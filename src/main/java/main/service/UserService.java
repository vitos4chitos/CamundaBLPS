package main.service;

import main.entity.Role;
import main.entity.User;

import java.util.List;

public interface UserService {

  User getUserById(Long id);
  User saveUser(User user);
  Long getUserIdByLogin(String login);
  User getUserByLogin(String login);
  void saveRole(Role role);
  List<User> findAll();
  Boolean existsUserByLogin(String login);
  void addRoleToUser(String login, String roleName);
  Role getRoleByName(String name);
}
