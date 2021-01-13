package com.snakyhy.snakymail.search.controller;

import com.snakyhy.snakymail.search.service.MallSearchService;
import com.snakyhy.snakymail.search.vo.SearchParam;
import com.snakyhy.snakymail.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model){
        SearchResult result=mallSearchService.search(searchParam);
        model.addAttribute("result",result);
        return "list";
    }
}
