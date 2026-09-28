package br.com.brasiltravel.brasiltravel_aereo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;

@Service
public class SenhaService {

    public String gerarHash(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder();
            for (byte b : hash) {
                resultado.append(String.format("%02x", b));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível.", e);
        }
    }

    public boolean conferirSenha(String senhaDigitada, String senhaArmazenada) {
        if (senhaDigitada == null || senhaArmazenada == null) {
            return false;
        }

        String hashDigitado = gerarHash(senhaDigitada);

        // Aceita hash para os novos cadastros e também texto simples para dados antigos da etapa 2.
        return hashDigitado.equals(senhaArmazenada) || senhaDigitada.equals(senhaArmazenada);
    }
}
