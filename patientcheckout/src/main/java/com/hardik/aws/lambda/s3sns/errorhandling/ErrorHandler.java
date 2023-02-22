package com.hardik.aws.lambda.s3sns.errorhandling;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

    public void handler(SNSEvent event){
        Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

        event.getRecords().forEach(record->{
            logger.info("Dead Letter Queue Event :" + record.toString());
        });
    }
}
