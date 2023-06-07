package com.nova.controllers;

import com.nova.models.Bonuses;
import com.nova.models.GameIncome;
import com.nova.models.Games;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SalesCont extends Global {

    @GetMapping("/sales")
    public String sales(Model model) {
        if (getRole().equals("USER")) return "redirect:/index";
        List<GameIncome> gameIncomes = repoGameIncome.findAllByUserid(getUser().getId());
        float income = 0;
        for (GameIncome g : gameIncomes) income += g.getIncome();

        model.addAttribute("income", income);
        model.addAttribute("games", gameIncomes);
        model.addAttribute("role", getRole());

        float action = 0;
        float shooter = 0;
        float strategy = 0;
        float rpg = 0;
        float mmo = 0;
        float simulator = 0;
        float mmorpg = 0;

        for (GameIncome gi : gameIncomes) {
            Games game = repoGames.getOne(gi.getGameid());
            switch (game.getGenre()) {
                case MMO -> mmo += gi.getIncome();
                case Action -> action += gi.getIncome();
                case MMORPG -> mmorpg += gi.getIncome();
                case RPG -> rpg += gi.getIncome();
                case Стратегия -> strategy += gi.getIncome();
                case Shooter -> shooter += gi.getIncome();
                case Симулятор -> simulator += gi.getIncome();
            }
        }

        model.addAttribute("action", action);
        model.addAttribute("strategy", strategy);
        model.addAttribute("shooter", shooter);
        model.addAttribute("rpg", rpg);
        model.addAttribute("mmo", mmo);
        model.addAttribute("simulator", simulator);
        model.addAttribute("mmorpg", mmorpg);

        List<Bonuses> bonusesList = repoBonuses.findAllByUserId(getUser().getId());

        int bonus = bonusesList.stream().reduce(0, (i, b) -> i + b.getBonus(), Integer::sum);

        model.addAttribute("bonuses", bonusesList);
        model.addAttribute("bonus", bonus);
        return "sales";
    }
}
