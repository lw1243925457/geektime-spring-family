package geektime.spring.springbucks.configs;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myconfig")
public class MyTestConfig {
    private String argv1;
    private String argv2;

    public String getArgv1() {
        return argv1;
    }

    public void setArgv1(String argv1) {
        this.argv1 = argv1;
    }

    public String getArgv2() {
        return argv2;
    }

    public void setArgv2(String argv2) {
        this.argv2 = argv2;
    }
}
