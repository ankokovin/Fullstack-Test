package ankokovin.fullstacktest.WebServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebServerApplication {
    // TODO(#4): настройка портов и https
    // TODO(#12): контроллер организаций
    // TODO(#13): контроллер работников
    public static void main(String[] args) {
        SpringApplication.run(WebServerApplication.class, args);
    }

}
