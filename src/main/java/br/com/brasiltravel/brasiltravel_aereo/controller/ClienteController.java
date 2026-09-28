package br.com.brasiltravel.brasiltravel_aereo.controller;

import br.com.brasiltravel.brasiltravel_aereo.dto.PerfilUsuarioForm;
import br.com.brasiltravel.brasiltravel_aereo.model.Usuario;
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
public class ClienteController {

    private final SessaoService sessaoService;
    private final AuthService authService;

    public ClienteController(SessaoService sessaoService, AuthService authService) {
        this.sessaoService = sessaoService;
        this.authService = authService;
    }

    @GetMapping("/cliente/perfil")
    public String perfil(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = sessaoService.usuarioLogado(session).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Faça login para acessar seu perfil.");
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "cliente/perfil";
    }

    @GetMapping("/cliente/perfil/editar")
    public String editarPerfil(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = sessaoService.usuarioLogado(session).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Faça login para editar seu perfil.");
            return "redirect:/login";
        }

        if (!model.containsAttribute("perfilForm")) {
            model.addAttribute("perfilForm", authService.criarFormPerfil(usuario));
        }
        return "cliente/editar-perfil";
    }

    @PostMapping("/cliente/perfil/editar")
    public String salvarPerfil(@Valid @ModelAttribute("perfilForm") PerfilUsuarioForm form,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = sessaoService.usuarioLogado(session).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Faça login para editar seu perfil.");
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "cliente/editar-perfil";
        }

        try {
            Usuario atualizado = authService.atualizarPerfil(usuario.getIdUsuario(), form);
            sessaoService.atualizarSessao(session, atualizado);
            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
            return "redirect:/cliente/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            redirectAttributes.addFlashAttribute("perfilForm", form);
            return "redirect:/cliente/perfil/editar";
        }
    }
}
