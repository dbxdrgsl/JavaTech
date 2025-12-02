package ro.uaic.dbxdrgsl.prefschedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PrefScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrefScheduleApplication.class, args);
	}

}
