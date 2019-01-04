package com.rkhd.sre.app.controller;

import com.rkhd.sre.app.interceptor.UserInterceptor;
import com.rkhd.sre.app.utils.BaseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    @Value("${security.user.name:admin}")
    private String username;
    @Value("${security.user.password:admin}")
    private String password;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "forward:/index";
    }


    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "") String catalog, @RequestParam(defaultValue = "") String configKey) {
        DecimalFormat df = new DecimalFormat("0.00");
        long totalMem = Runtime.getRuntime().totalMemory();
        long maxMem = Runtime.getRuntime().maxMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        model.addAttribute("totalMem", df.format(totalMem / 1000000F) + " MB");
        model.addAttribute("maxMem", df.format(maxMem / 1000000F) + " MB");
        model.addAttribute("freeMem", df.format(freeMem / 1000000F) + " MB");
        model.addAttribute("uptime", BaseUtil.toDuration());
        return "/main";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }





    @RequestMapping(value = "/loginPost", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> loginPost(String username, String password, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        if (!password.equals(password)) {
            map.put("success", false);
            map.put("message", "密码错误");
            return map;
        }

        // 设置session
        session.setAttribute(UserInterceptor.SESSION_KEY, username);

        map.put("success", true);
        map.put("message", "登录成功");
        return map;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 移除session
        session.removeAttribute(UserInterceptor.SESSION_KEY);
        return "redirect:/login";
    }
}
