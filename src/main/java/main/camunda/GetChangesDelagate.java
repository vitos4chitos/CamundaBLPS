package main.camunda;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import main.camunda.response.Response;
import main.entity.ChangeAnswer;
import main.service.EditService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChangesDelagate implements JavaDelegate {

    private final EditService editService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String)delegateExecution.getVariable("login");
        String name = (String)delegateExecution.getVariable("name");
        String status = (String)delegateExecution.getVariable("status");
        ResponseEntity<List<ChangeAnswer>> entity = editService.getChanges(login, name, status);
        List<ChangeAnswer> answers = entity.getBody();
        Gson gson = new Gson();
        String jsonAnswers = gson.toJson(answers);
        Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), jsonAnswers);
        ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
        delegateExecution.setVariable("answer", jsonText);

    }
}
