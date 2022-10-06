package main.camunda;

import lombok.RequiredArgsConstructor;
import main.camunda.response.Response;
import main.entity.Role;
import main.entity.User;
import main.service.UserServiceImpl;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Registration implements JavaDelegate {

    private final UserServiceImpl userService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        String password = (String) delegateExecution.getVariable("password");
        ResponseEntity<String> entity;
        if (userService.existsUserByLogin(login)) {
            entity = new ResponseEntity<>("Пользователь с таким именем существует !", HttpStatus.CONFLICT);
            Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), entity.getBody());
            ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
            delegateExecution.setVariable("error", jsonText);
            throw new BpmnError("thisLoginAlreadyExists");
        }
        else {
            List<Role> roles = new ArrayList<>();
            roles.add(userService.getRoleByName("ROLE_READER"));
            User user = new User();
            user.setPassword(password);
            user.setLogin(login);
            userService.saveUser(user);
            entity = new ResponseEntity<>("Успешно зарегестрированны !", HttpStatus.CREATED);
            Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), entity.getBody());
            ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
            delegateExecution.setVariable("answer", jsonText);
        }

    }
}
