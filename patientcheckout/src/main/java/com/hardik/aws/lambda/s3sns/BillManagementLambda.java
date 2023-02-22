package com.hardik.aws.lambda.s3sns;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillManagementLambda {

    private final ObjectMapper objectMapper = new ObjectMapper();
    public void handler(SNSEvent event){
        event.getRecords().forEach(record->{
            try {
                PatientCheckoutEvent patientCheckoutEvent = objectMapper.
                        readValue(record.getSNS().getMessage(), PatientCheckoutEvent.class);
                System.out.println(patientCheckoutEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
