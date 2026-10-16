package com.grupo4.gutti;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Prueba de Carga del Contexto Spring Boot")
class GuttiApplicationTests {

	@Test
	@DisplayName("contextLoads() - Debe cargar el contexto de la aplicación sin errores")
	void contextLoads() {
	}

}
