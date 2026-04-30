package ms.usuario.msusuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient
@SpringBootApplication
public class MsusuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsusuarioApplication.class, args);
	}

}
