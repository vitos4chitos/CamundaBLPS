package main.camunda;

import lombok.RequiredArgsConstructor;
import main.camunda.response.Response;
import main.entity.Verdict;
import main.service.ApproveService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerdictDelegate implements JavaDelegate {

    private final ApproveService approveService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Verdict verdict = new Verdict();
        verdict.setResponseVerdictAns((String) delegateExecution.getVariable("ResponseAnswer"));
        verdict.setComment((String) delegateExecution.getVariable("comment"));
        verdict.setIs_confirmed((String) delegateExecution.getVariable("isConfirmed"));
        verdict.setPageName((String) delegateExecution.getVariable("pagename"));
        verdict.setWhoToConfirm((String) delegateExecution.getVariable("whotoconfirm"));
        String login = (String) delegateExecution.getVariable("login");
        Verdict verdictAns = approveService.approve(verdict, login);
        ResponseEntity<String> entity;
        if (verdictAns.getResponseVerdictAns().contains("ошибка")) {
            entity = new ResponseEntity<>(verdictAns.getResponseVerdictAns(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            if (verdictAns.getResponseVerdictAns().contains("Статус изменился"))
                entity = new ResponseEntity<>(verdictAns.getResponseVerdictAns(), HttpStatus.ACCEPTED);
            else
                entity = new ResponseEntity<>(verdictAns.getResponseVerdictAns(), HttpStatus.NOT_ACCEPTABLE);
        }
        Response response = new Response(entity.getStatusCodeValue(), entity.getStatusCode().toString(), entity.
                getBody());
        ObjectValue jsonText = Variables.objectValue(response).serializationDataFormat("application/json").create();
        delegateExecution.setVariable("answer", jsonText);
    }
}
