package com.extensao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicacao Spring Boot.
 *
 * A anotacao @SpringBootApplication liga tres coisas:
 *  - @Configuration: esta classe pode definir "beans"
 *  - @EnableAutoConfiguration: o Spring configura sozinho o Tomcat, JPA, H2...
 *  - @ComponentScan: o Spring varre o pacote com.extensao procurando
 *    @Service, @RestController, @Repository, etc. e cria/injeta tudo.
 *
 * Repare: este metodo main substitui o antigo Main.java do console.
 */
@SpringBootApplication
public class GerenciamentoExtensaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GerenciamentoExtensaoApplication.class, args);
    }
}
