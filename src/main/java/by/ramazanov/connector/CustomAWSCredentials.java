package by.ramazanov.connector;

import com.amazonaws.auth.AWSCredentials;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ApiModel(description = "Custom AWS credentials implementation")
public class CustomAWSCredentials implements AWSCredentials {
    @ApiModelProperty(value = "AWS Access Key", required = true)
    private final String accessKey;

    @ApiModelProperty(value = "AWS Secret Key", required = true)
    private final String secretKey;

    /**
     * Constructs an instance of CustomAWSCredentials with the specified access key and secret key.
     *
     * @param accessKey the AWS access key
     * @param secretKey the AWS secret key
     */
    public CustomAWSCredentials(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        log.debug("CustomAWSCredentials initialized with accessKey: {}", accessKey);
    }

    @Override
    public String getAWSAccessKeyId() {
        log.debug("Retrieving AWS Access Key ID");
        return accessKey;
    }

    @Override
    public String getAWSSecretKey() {
        log.debug("Retrieving AWS Secret Key");
        return secretKey;
    }
}
