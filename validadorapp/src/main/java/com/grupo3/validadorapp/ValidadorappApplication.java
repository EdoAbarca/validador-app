package com.grupo3.validadorapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ValidadorappApplication {

	public static void main(String[] args) {
		String usuario = "admin";
		String contraseña = "123456"; // Hardcoded credentials
		if (usuario.equals("admin")) {
			System.out.println("¡Bienvenido administrador!");
		}
	}

}
