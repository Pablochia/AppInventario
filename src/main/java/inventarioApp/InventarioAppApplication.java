package inventarioApp;

import controlador.ControladorProducto;
import modelo.RepositorioProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import vista.Principal;

@SpringBootApplication
@ComponentScan("modelo")
@EnableJdbcRepositories("modelo")
public class InventarioAppApplication {

	@Autowired
        RepositorioProducto repoProducto;
        
        public static void main(String[] args){
            SpringApplicationBuilder builder= new SpringApplicationBuilder(InventarioAppApplication.class);
            builder.headless(false);
            ConfigurableApplicationContext context=builder.run(args);   
        }
        @Bean
        ApplicationRunner applicationRunner(){
            return args ->{
                Principal vista= new Principal();
                ControladorProducto controlador=new ControladorProducto(repoProducto,vista);
            };
        }

}
