package main.camunda;

import lombok.RequiredArgsConstructor;
import main.entity.Role;
import main.service.UserServiceImpl;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthDelegate implements JavaDelegate {

    private final UserServiceImpl userService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception{
        String login = (String) delegateExecution.getVariable("login");
        String password = (String) delegateExecution.getVariable("password");
        String answer = userService.checkUser(login, password);

        if(!answer.equals("YES")){
            delegateExecution.setVariable("Error", "401 Unauthorized");
            throw new BpmnError("NoSuchUser");
        }
        else{
            delegateExecution.removeVariable("Error");
            List<Role> roleList = userService.getRolesByLogin(login);
            for(int i = 0; i < roleList.size(); i++){
                System.out.println(roleList.get(i).getName());
            }
        }


    }
}
