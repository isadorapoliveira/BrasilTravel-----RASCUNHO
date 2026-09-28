package br.com.brasiltravel.brasiltravel_aereo.controller;

import br.com.brasiltravel.brasiltravel_aereo.dto.CadastroUsuarioForm;
import br.com.brasiltravel.brasiltravel_aereo.dto.LoginForm;
import br.com.brasiltravel.brasiltravel_aereo.model.Usuario;
import br.com.brasiltravel.brasiltravel_aereo.model.enums.TipoUsuario;
import br.com.brasiltravel.brasiltravel_aereo.service.AuthService;
import br.com.brasiltravel.brasiltravel_aereo.service.SessaoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;
    private final SessaoService sessaoService;

    public AuthController(AuthService authService, SessaoService sessaoService) {
        this.authService = authService;
        this.sessaoService = sessaoService;
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        if (sessaoService.estaLogado(session)) {
            return redirecionarPorPerfil(session);
        }

        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@Valid @ModelAttribute("loginForm") LoginForm form,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        return authService.autenticar(form.getEmail(), form.getSenha())
                .map(usuario -> {
                    sessaoService.iniciarSessao(session, usuario);
                    return usuario.getTipoUsuario() == TipoUsuario.ADMIN
                            ? "redirect:/admin"
                            : "redirect:/cliente/perfil";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "E-mail ou senha inválidos.");
                    redirectAttributes.addFlashAttribute("loginForm", form);
                    return "redirect:/login";
                });
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model, HttpSession session) {
        if (sessaoService.estaLogado(session)) {
            return redirecionarPorPerfil(session);
        }

        if (!model.containsAttribute("cadastroForm")) {
            model.addAttribute("cadastroForm", new CadastroUsuarioForm());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("cadastroForm") CadastroUsuarioForm form,
                            BindingResult bindingResult,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "cadastro";
        }

        try {
            Usuario usuario = authService.cadastrarCliente(form);
            sessaoService.iniciarSessao(session, usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso.");
            return "redirect:/cliente/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            redirectAttributes.addFlashAttribute("cadastroForm", form);
            return "redirect:/cadastro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        sessaoService.encerrarSessao(session);
        redirectAttributes.addFlashAttribute("sucesso", "Sessão encerrada com sucesso.");
        return "redirect:/login";
    }

    private String redirecionarPorPerfil(HttpSession session) {
        return sessaoService.ehAdmin(session) ? "redirect:/admin" : "redirect:/cliente/perfil";
    }
}
