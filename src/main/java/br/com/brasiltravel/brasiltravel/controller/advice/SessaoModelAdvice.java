package br.com.brasiltravel.brasiltravel.controller.advice;

import br.com.brasiltravel.brasiltravel.service.SessaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessaoModelAdvice {

    @ModelAttribute("usuarioLogadoNome")
    public Object usuarioLogadoNome(HttpSession session) {
        return session.getAttribute(SessaoService.NOME_USUARIO);
    }

    @ModelAttribute("usuarioLogadoTipo")
    public Object usuarioLogadoTipo(HttpSession session) {
        return session.getAttribute(SessaoService.TIPO_USUARIO);
    }

    @ModelAttribute("usuarioEstaLogado")
    public boolean usuarioEstaLogado(HttpSession session) {
        return session.getAttribute(SessaoService.ID_USUARIO) != null;
    }

    @ModelAttribute("usuarioEhAdmin")
    public boolean usuarioEhAdmin(HttpSession session) {
        Object tipo = session.getAttribute(SessaoService.TIPO_USUARIO);
        return "ADMIN".equals(tipo);
    }
    @ModelAttribute("requestUri")
    public String requestUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

}
