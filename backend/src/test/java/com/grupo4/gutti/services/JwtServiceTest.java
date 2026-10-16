package com.grupo4.gutti.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas Unitarias de JwtService")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    // Clave secreta Base64 de 256 bits para tests
    private static final String SECRET_KEY = "NDBhZTYzNTI2NjU1NmE1ODZlMzI3MjM1NzU4ODc4MmY0MTNmNDQyODQ3MmI0YjYyNTA2NDUzNjc1NjZiNTk3MA==";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        userDetails = userDetailsMock("franco@gutti.com");
    }

    // ==========================================
    // MÉTODOS HELPER (Para evitar duplicación)
    // ==========================================

    private UserDetails userDetailsMock(String username) {
        return new User(username, "password", Collections.emptyList());
    }

    // ==========================================
    // PRUEBAS DE GENERACIÓN Y VALIDACIÓN JWT
    // ==========================================

    @Test
    @DisplayName("generarToken() - Debe generar un token válido y permitir extraer el email correctamente")
    void generarToken_conUserDetailsValido_permiteExtraerEmail() {
        // Act
        String token = jwtService.generarToken(userDetails);

        // Assert
        assertThat(token).isNotNull().isNotBlank();
        String emailExtraido = jwtService.extraerEmail(token);
        assertThat(emailExtraido).isEqualTo("franco@gutti.com");
    }

    @Test
    @DisplayName("esTokenValido() - Debe retornar true cuando el token corresponde al usuario y no está expirado")
    void esTokenValido_conUsuarioCorrecto_retornaTrue() {
        // Arrange
        String token = jwtService.generarToken(userDetails);

        // Act
        boolean esValido = jwtService.esTokenValido(token, userDetails);

        // Assert
        assertThat(esValido).isTrue();
    }

    @Test
    @DisplayName("esTokenValido() - Debe retornar false cuando el token no coincide con el usuario proporcionado")
    void esTokenValido_conUsuarioDiferente_retornaFalse() {
        // Arrange
        String token = jwtService.generarToken(userDetails);
        UserDetails otroUsuario = userDetailsMock("otro@gutti.com");

        // Act
        boolean esValido = jwtService.esTokenValido(token, otroUsuario);

        // Assert
        assertThat(esValido).isFalse();
    }
}
