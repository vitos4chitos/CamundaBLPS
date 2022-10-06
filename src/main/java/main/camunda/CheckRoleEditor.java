package main.camunda;

import lombok.RequiredArgsConstructor;
import main.entity.Role;
import main.service.UserServiceImpl;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.rest.sub.identity.impl.UserResourceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckRoleEditor implements JavaDelegate {

    private final UserServiceImpl userService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        List<Role> roleList = userService.getRolesByLogin(login);
        boolean flag = true;
        for(Role role: roleList){
            if (role.getName().equals("ROLE_EDITOR") || role.getName().equals("ROLE_ADMIN")) {
                flag = false;
                break;
            }
        }
        if(flag){
            throw new BpmnError("NoAccessRights");
        }
    }
}
