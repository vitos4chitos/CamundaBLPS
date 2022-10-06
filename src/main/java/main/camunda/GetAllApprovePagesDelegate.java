package main.camunda;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import main.camunda.response.Response;
import main.entity.Notification;
import main.entity.Page;
import main.service.ApproveService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllApprovePagesDelegate  implements JavaDelegate {

    private final ApproveService approveService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        ResponseEntity<?> entity = approveService.getApprovePages(login);
        if(entity.getBody().equals("Пользователя с логином " + login + " не существует !")){
            Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), "Пользователя с логином " + login + " не существует !");
            ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
            delegateExecution.setVariable("answer", jsonText);
        }
        else{
            List<Page>notificationList = (List<Page>) entity.getBody();
            Gson gson = new Gson();
            String jsonAnswers = gson.toJson(notificationList);
            Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), jsonAnswers);
            ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
            delegateExecution.setVariable("answer", jsonText);
        }
    }
}
