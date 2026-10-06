package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Testes unitarios do model: sem banco, sem Spring (Aula 15).
public class BanhoTest {

    private Banho banhoDoRex() {
        return new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.of(2026, 10, 1, 10, 0));
    }

    @Test
    public void deveAcumular20PontosDeFidelidade() {
        // Act
        int pontos = banhoDoRex().calcularPontosFidelidade();

        // Assert
        assertEquals(20, pontos);
    }

    @Test
    public void deveDurar45Minutos() {
        // Act
        int duracao = banhoDoRex().getDuracaoMinutos();

        // Assert
        assertEquals(45, duracao);
    }

    @Test
    public void deveCalcularPrecosCorretosPorPorte() {
        Banho pequeno = new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.now());
        Banho medio = new Banho(2, "Rex", "MEDIO", "Ana", LocalDateTime.now());
        Banho grande = new Banho(3, "Rex", "GRANDE", "Ana", LocalDateTime.now());

        assertEquals(60.0, pequeno.calcularPreco());
        assertEquals(80.0, medio.calcularPreco());
        assertEquals(100.0, grande.calcularPreco());
    }
}
