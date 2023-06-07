package com.nova.controllers;

import com.nova.models.Bonuses;
import com.nova.models.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bonus")
public class BonusCont extends Global {

    @GetMapping
    public String bonus(Model model) {
        model.addAttribute("users", repoUsers.findAllByRole(Role.PUB));
        model.addAttribute("role", getRole());
        model.addAttribute("roles", Role.values());
        return "bonus";
    }

    @PostMapping("/{id}")
    public String bonusAdd(@PathVariable Long id, @RequestParam int bonus) {
        repoBonuses.save(new Bonuses(repoUsers.getOne(id).getId(), getUser().getUsername(), dateNow(), bonus));
        return "redirect:/bonus";
    }
}
