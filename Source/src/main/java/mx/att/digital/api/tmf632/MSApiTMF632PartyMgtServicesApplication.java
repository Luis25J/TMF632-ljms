package mx.att.digital.api.tmf632;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * The type Ms api tmf 632 party mgt services application.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MSApiTMF632PartyMgtServicesApplication {
	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(MSApiTMF632PartyMgtServicesApplication.class, args);
	}

}
