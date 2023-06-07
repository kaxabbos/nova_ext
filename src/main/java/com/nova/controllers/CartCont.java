package com.nova.controllers;

import com.nova.models.GameIncome;
import com.nova.models.Games;
import com.nova.models.Users;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartCont extends Global {

    @GetMapping("/cart")
    public String cart(Model model) {
        Users userFromDB = getUser();

        if (userFromDB.getCart() != null) {
            long[] cart = userFromDB.getCart();
            float summary = 0;
            List<Games> games = new ArrayList<>(), temp = repoGames.findAll();

            for (Games g : temp) for (long c : cart) if (g.getId() == c) games.add(g);
            for (Games g : games) summary += g.getPrice();

            model.addAttribute("summary", summary);
            model.addAttribute("games", games);
            int i = 0;
            for (Games ignored : games) {
                i++;
                if (i == 2) {
                    model.addAttribute("more", i);
                    break;
                }
            }
        }
        model.addAttribute("role", getRole());
        return "cart";
    }

    @PostMapping("/game/{id}/add_cart")
    public String add_cart(@PathVariable(value = "id") Long id) {
        Users userFromDB = getUser();

        long[] cart;
        if (userFromDB.getCart() == null) cart = new long[]{id};
        else {
            cart = new long[userFromDB.getCart().length + 1];
            System.arraycopy(userFromDB.getCart(), 0, cart, 0, userFromDB.getCart().length);
            cart[userFromDB.getCart().length] = id;
        }

        userFromDB.setCart(cart);

        repoUsers.save(userFromDB);
        return "redirect:/game/{id}";
    }

    @PostMapping("/game/{id}/remove_cart")
    public String remove_cart(@PathVariable(value = "id") Long id) {
        Users userFromDB = getUser();

        if (userFromDB.getCart().length == 1) userFromDB.setCart(null);
        else {
            long[] cart = new long[userFromDB.getCart().length - 1];
            int i = 0;
            for (long c : userFromDB.getCart()) {
                if (id == c) continue;
                cart[i] = c;
                i++;
            }
            userFromDB.setCart(cart);
        }

        repoUsers.save(userFromDB);
        return "redirect:/cart";
    }

    @PostMapping("/game/remove_cart_all")
    public String remove_cart_all() {
        Users userFromDB = getUser();

        userFromDB.setCart(null);

        repoUsers.save(userFromDB);
        return "redirect:/cart";
    }

    @PostMapping("/game/{id}/buy")
    public String buy(@PathVariable Long id) {
        Users userFromDB = getUser();

        if (userFromDB.getCart() != null) {
            if (userFromDB.getCart().length == 1) userFromDB.setCart(null);
            else {
                long[] cart = new long[userFromDB.getCart().length - 1];
                int i = 0;
                for (long c : userFromDB.getCart()) {
                    if (id == c) continue;
                    cart[i] = c;
                    i++;
                }
                userFromDB.setCart(cart);
            }
        }

        long[] buy;
        if (userFromDB.getBuy() == null) buy = new long[]{id};
        else {
            buy = new long[userFromDB.getBuy().length + 1];
            System.arraycopy(userFromDB.getBuy(), 0, buy, 0, userFromDB.getBuy().length);
            buy[userFromDB.getBuy().length] = id;
        }
        userFromDB.setBuy(buy);

        GameIncome gi = repoGameIncome.findByGameid(id);

        gi.setCount(gi.getCount() + 1);
        float tax = gi.getPrice() * TAX;
        gi.setTax(gi.getTax() + tax);
        gi.setIncome(gi.getIncome() + (gi.getPrice() - tax));

        repoGameIncome.save(gi);
        repoUsers.save(userFromDB);
        return "redirect:/game/{id}";
    }

    @PostMapping("/game/buy_cart_all")
    public String buy_cart_all() {
        Users userFromDB = getUser();
        long[] buy;
        if (userFromDB.getBuy() == null) {
            buy = new long[userFromDB.getCart().length];
            for (int i = 0; i < userFromDB.getCart().length; i++) {
                buy[i] = userFromDB.getCart()[i];

                GameIncome gi = repoGameIncome.findByGameid(userFromDB.getCart()[i]);
                gi.setCount(gi.getCount() + 1);
                float tax = gi.getPrice() * TAX;
                gi.setTax(gi.getTax() + tax);
                gi.setIncome(gi.getIncome() + (gi.getPrice() - tax));
                repoGameIncome.save(gi);
            }

        } else {
            buy = new long[userFromDB.getBuy().length + userFromDB.getCart().length];
            for (int i = 0; i < buy.length; i++) {
                for (int j = 0; j < userFromDB.getBuy().length; j++) {
                    buy[i] = userFromDB.getBuy()[j];
                    i++;
                }
                for (int j = 0; j < userFromDB.getCart().length; j++) {
                    buy[i] = userFromDB.getCart()[j];
                    GameIncome gi = repoGameIncome.findByUserid(userFromDB.getCart()[j]);
                    gi.setCount(gi.getCount() + 1);
                    float tax = gi.getPrice() * TAX;
                    gi.setTax(gi.getTax() + tax);
                    gi.setIncome(gi.getIncome() + (gi.getPrice() - tax));repoGameIncome.save(gi);
                    i++;
                }
            }
        }

        userFromDB.setBuy(buy);
        userFromDB.setCart(null);

        repoUsers.save(userFromDB);

        return "redirect:/cart";
    }
}
