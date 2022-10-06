package main.camunda;

import lombok.RequiredArgsConstructor;
import main.camunda.response.Response;
import main.entity.Request;
import main.service.PageService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Component
@RequiredArgsConstructor
public class AddPageDelegate implements JavaDelegate {

    private final PageService pageService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Request request = new Request();
        request.setText((String) delegateExecution.getVariable("text"));
        request.setName((String) delegateExecution.getVariable("name"));
        request.setComment("somecomment");
        String login = (String) delegateExecution.getVariable("login");
        ResponseEntity<String> entity = pageService.addPage(login, request);
        Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), entity.getBody());
        ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
        delegateExecution.setVariable("answer", response);
    }

}
