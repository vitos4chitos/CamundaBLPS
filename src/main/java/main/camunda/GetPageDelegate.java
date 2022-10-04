package main.camunda;

import lombok.RequiredArgsConstructor;
import main.service.SearchService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import javax.inject.Named;


@Named
@RequiredArgsConstructor
public class GetPageDelegate implements JavaDelegate {

    private final SearchService searchService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String name = (String) delegateExecution.getVariable("name");
        System.out.println(name);
        String text = searchService.getAnswer(name);
        if(text == null){
            delegateExecution.removeVariable("name");
            throw new BpmnError("NoSuchPage");
        }
        else {
            delegateExecution.setVariable("text", text);
        }

    }

}
