package br.com.brasiltravel.brasiltravel.controller;

import br.com.brasiltravel.brasiltravel.repository.DestinoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DestinoRepository destinoRepository;

    public HomeController(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("destinosDestaque", destinoRepository.findByAtivoTrueOrderByCidadeAsc().stream().limit(6).toList());
        return "index";
    }
}
