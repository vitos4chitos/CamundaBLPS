package main.camunda;

import lombok.RequiredArgsConstructor;
import main.service.SearchService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.h2.expression.Variable;
import org.springframework.stereotype.Component;

import javax.inject.Named;


@Component
@RequiredArgsConstructor
public class GetPageDelegate implements JavaDelegate {

    private final SearchService searchService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String name = (String) delegateExecution.getVariable("name");
        String text = searchService.getAnswer(name);
        if(text == null){
            delegateExecution.removeVariable("name");
            throw new BpmnError("NoSuchPage");
        }
        else {
            ObjectValue jsonText = Variables.objectValue(name).serializationDataFormat("application/json").create();
            delegateExecution.setVariable("text", jsonText);
        }

    }

}
