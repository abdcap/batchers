package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.service.TaxPaymentWebService;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallWebserviceProcessor implements ItemProcessor<Employee, Employee> {

    @Autowired
    private TaxPaymentWebService taxPaymentWebService;

    @Value("${taxProcessor.retry.initialInterval:100}")
    private long initialInterval = 100;

    @Value("${taxProcessor.retry.maxAtempts:3}")
    private int maxAtempts = 3;

    @Override
    public Employee process(Employee employee) throws Exception {
        System.out.println("Web service process: " + employee);
        return createRetryTemplate().execute(retryContext -> taxPaymentWebService.doWebserviceCallToTaxService(employee));
    }

    private RetryTemplate createRetryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
        exceptions.put(TaxWebServiceException.class, true);

        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAtempts, exceptions);
        template.setRetryPolicy(retryPolicy);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialInterval);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }
}
