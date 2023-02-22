package com.hardik.aws.lambda.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class PatientCheckoutLambda {

    private static final String PATIENT_CHECKOUT_TOPIC = System.getenv("PATIENT_CHECKOUT_TOPIC");

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
    public void handler(S3Event event, Context context) throws InterruptedException {

//        LambdaLogger logger = context.getLogger();

        Logger logger = LoggerFactory.getLogger(PatientCheckoutLambda.class);

        event.getRecords().forEach(record->{
            S3ObjectInputStream s3InputStream = s3
                            .getObject(record.getS3().getBucket().getName(), record.getS3().getObject().getKey())
                            .getObjectContent();

            try {
                logger.info("Reading Data From S3");
                List<PatientCheckoutEvent> patientCheckoutEvents = Arrays
                        .asList(objectMapper.readValue(s3InputStream, PatientCheckoutEvent[].class));

                logger.info(patientCheckoutEvents.toString());

                s3InputStream.close();

                logger.info("Sending message to SNS");

                publishMessageToSNS(patientCheckoutEvents);
            } catch (IOException e) {
                logger.error("Exception is :" + e);
                throw new RuntimeException("Something went wrong: " + e);
            }
        });


        
    }

    private void publishMessageToSNS(List<PatientCheckoutEvent> patientCheckoutEvents) {
        patientCheckoutEvents.forEach(checkout->{
            try {
                sns.publish(PATIENT_CHECKOUT_TOPIC,objectMapper.writeValueAsString(checkout));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
