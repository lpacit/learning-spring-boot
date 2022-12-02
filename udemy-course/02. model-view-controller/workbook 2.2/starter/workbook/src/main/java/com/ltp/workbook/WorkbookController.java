package com.ltp.workbook;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
public class WorkbookController {
    
    @GetMapping("/")
    public String getShows(Model model) {
        model.addAllAttributes(List.of(
                new Show("Breaking Bad", "Ozymandias", 10),
                new Show("Attack on Titan", "Hero", 9.9),
                new Show("Attack on Titan", "Perfect Game", 9.9),
                new Show("Star Wars: The Clone Wars", "Victory and Death", 9.9),
                new Show("Mr. Robot", "407 Proxy Authentication Required", 9.9)
        ));
        return "shows";
    }

}
