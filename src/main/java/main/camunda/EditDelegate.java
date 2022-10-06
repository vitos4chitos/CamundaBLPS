package main.camunda;

import lombok.RequiredArgsConstructor;
import main.camunda.response.Response;
import main.entity.Request;
import main.service.EditService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditDelegate implements JavaDelegate {


    private final EditService editService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Request request = new Request();
        request.setText((String) delegateExecution.getVariable("text"));
        request.setName((String) delegateExecution.getVariable("name"));
        request.setComment("somecomment");
        System.out.println(request.getText() + " " + request.getName());
        String login = (String) delegateExecution.getVariable("login");
        ResponseEntity<String> entity = editService.editWithApprove(login, request);
        Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), entity.getBody());
        ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
        delegateExecution.setVariable("answer", jsonText);

    }
}
