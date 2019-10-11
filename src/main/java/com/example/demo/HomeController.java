package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Message message, BindingResult result, @ModelAttribute Message messagec,
                              @RequestParam("file") MultipartFile file ) {
        if (result.hasErrors()) {
            return "messageform";
        }

        if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResults = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            messagec.setHeadshot(uploadResults.get("url").toString());
            messageRepository.save(messagec);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }

        messageRepository.save(message);
        return "redirect:/";

    }
}
