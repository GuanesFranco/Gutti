package com.grupo4.gutti.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    // Clave secreta Base64 de 256 bits para tests
    private static final String SECRET_KEY = "NDBhZTYzNTI2NjU1NmE1ODZlMzI3MjM1NzU4ODc4MmY0MTNmNDQyODQ3MmI0YjYyNTA2NDUzNjc1NjZiNTk3MA==";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);

        userDetails = new User("franco@gutti.com", "password", Collections.emptyList());
    }

    @Test
    @DisplayName("Debe generar un token válido y permitir extraer el email correctamente")
    void generarToken_Y_extraerEmail() {
        String token = jwtService.generarToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String emailExtraido = jwtService.extraerEmail(token);
        assertEquals("franco@gutti.com", emailExtraido);
    }

    @Test
    @DisplayName("Debe retornar true cuando el token corresponde al usuario y no está expirado")
    void esTokenValido_ConUsuarioCorrecto_RetornaTrue() {
        String token = jwtService.generarToken(userDetails);

        boolean esValido = jwtService.esTokenValido(token, userDetails);

        assertTrue(esValido);
    }

    @Test
    @DisplayName("Debe retornar false cuando el token no coincide con el usuario proporcionado")
    void esTokenValido_ConUsuarioDiferente_RetornaFalse() {
        String token = jwtService.generarToken(userDetails);
        UserDetails otroUsuario = new User("otro@gutti.com", "password", Collections.emptyList());

        boolean esValido = jwtService.esTokenValido(token, otroUsuario);

        assertFalse(esValido);
    }
}
