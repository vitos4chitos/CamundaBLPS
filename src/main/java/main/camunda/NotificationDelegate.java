package main.camunda;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import main.camunda.response.Response;
import main.entity.Notification;
import main.service.NotificationService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationDelegate  implements JavaDelegate {

    private final NotificationService notificationService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String)delegateExecution.getVariable("login");
        boolean flag = (boolean) delegateExecution.getVariable("flag");
        ResponseEntity<?> entity;
        if(flag) {
            entity = notificationService.getAllNotificationsToRead(login);
        }
        else {
            entity = notificationService.getAllNotifications(login);
        }
        if(entity.getBody().equals("Пользователя с логином " + login + " не существует !")){
            Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), "Пользователя с логином " + login + " не существует !");
            ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
            delegateExecution.setVariable("answer", jsonText);
        }
        else{
            List<Notification> notificationList = (List<Notification>) entity.getBody();
            Gson gson = new Gson();
            String jsonAnswers = gson.toJson(notificationList);
            Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), jsonAnswers);
            ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
            delegateExecution.setVariable("answer", jsonText);
        }

    }

}

