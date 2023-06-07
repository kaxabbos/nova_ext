package com.nova.controllers;

import com.nova.models.Games;
import com.nova.models.Users;
import com.nova.models.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserListCont extends Global {

    @GetMapping("/userList")
    public String userList(Model model) {
        model.addAttribute("users", repoUsers.findAll());
        model.addAttribute("role", getRole());
        model.addAttribute("roles", Role.values());
        return "userList";
    }

    @PostMapping("/userList/{id}/edit")
    public String userUpdate(@PathVariable(value = "id") Long id, @RequestParam String username, @RequestParam String password, @RequestParam Role role) {
        Users temp = repoUsers.findById(id).orElseThrow();

        if (temp == getUser()) {
            if (String.valueOf(temp.getRole()).equals("ADMIN")) {
                temp.setUsername(username);
                temp.setPassword(password);
                temp.setRole(role);
                repoUsers.save(temp);
                return "redirect:/userList";
            } else if (!String.valueOf(temp.getRole()).equals("USER")) {
                temp.setUsername(username);
                temp.setPassword(password);
                temp.setRole(role);

                List<Games> gamesList = repoGames.findAllByUserid(getUser().getId());
                for (Games g : gamesList) {
                    totalGameDelete(g.getId());
                }

                repoUsers.save(temp);
                return "redirect:/index";
            } else {
                temp.setUsername(username);
                temp.setPassword(password);
                temp.setRole(role);
                repoUsers.save(temp);
                return "redirect:/index";
            }
        }

        if ((temp.getRole() == Role.ADMIN || temp.getRole() == Role.PUB) && role == Role.USER) {
            List<Games> gamesList = repoGames.findAllByUserid(temp.getId());
            for (Games g : gamesList) {
                totalGameDelete(g.getId());
            }
        }

        temp.setUsername(username);
        temp.setPassword(password);
        temp.setRole(role);
        repoUsers.save(temp);

        return "redirect:/userList";
    }

    @GetMapping("/userList/{id}/delete")
    public String userDelete(Model model, @PathVariable(value = "id") Long id) {
        Users temp = repoUsers.findById(id).orElseThrow();
        Users check = getUser();

        if (temp == check) {
            model.addAttribute("users", repoUsers.findAll());
            model.addAttribute("role", check.getRole());
            model.addAttribute("message", "Вы не можете удалить себя!");
            model.addAttribute("roles", Role.values());
            return "userList";
        }

        if (temp.getRole() == Role.ADMIN || temp.getRole() == Role.PUB) {
            List<Games> gamesList = repoGames.findAllByUserid(temp.getId());
            for (Games g : gamesList) {
                totalGameDelete(g.getId());
            }
        }

        repoUsers.deleteById(id);

        return "redirect:/userList";
    }
}
