package za.co.assessment.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
@Slf4j
public class WithdrawalEventPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    @Value("${sns.topic.arn:default-topic-arn}")
    private String topicArn;

    public WithdrawalEventPublisher(SnsClient snsClient) {
        this.snsClient = snsClient;
        this.objectMapper = new ObjectMapper();
    }

    public void publish(WithdrawalEvent event) {
        try {
            String message = event.toJson();
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();
            snsClient.publish(request);
            log.info("Message published {}: {}", topicArn, message);
        } catch (Exception e) {
            throw new RuntimeException("Error publishing withdrawal event", e);
        }
    }
}