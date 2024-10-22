package com.project.course.subscription.controller;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("api/admin/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/subscriptions")
    public String getAllActiveSubscriptions(Model model) {
        List<Subscription> subscriptions = subscriptionService.getAllActiveSubscriptions();
        model.addAttribute("subscriptions",subscriptions);
        return "subscriptions/index";
    }

    @GetMapping("/subscriptions/new")
    public String createSubscriptionForm(Model model) {
        Subscription subscription = new Subscription();
        model.addAttribute("subscription",subscription);
        return "subscriptions/create_subscription";
    }

    @PostMapping("/subscriptions")
    public String createSubscription(@Valid @ModelAttribute("subscription") Subscription subscription, BindingResult result) {
        if (result.hasErrors()) {
            return "subscriptions/create_subscription";
        }
        subscriptionService.createSubscription(subscription);
        return "redirect:/api/admin/subscription/subscriptions";
    }

    @GetMapping("/edit/{uuid}")
    public String editSubscription(@PathVariable String uuid, Model model) {
        Optional<SubscriptionDTO> optionalSubscription = subscriptionService.getSubscriptionByUuid(uuid);

        if (optionalSubscription.isPresent()) {
            SubscriptionDTO subscription = optionalSubscription.get();
            model.addAttribute("subscription", subscription);
            return "subscriptions/edit_subscription";
        } else {
            return "redirect:/api/admin/subscription/subscriptions";
        }
    }

    @PostMapping("/subscriptions/{uuid}")
    public String updateSubscription(@PathVariable String uuid, @Valid @ModelAttribute("subscription") Subscription subscription, BindingResult result) {
        if (result.hasErrors()) {
            return "subscriptions/edit_subscription";
        }
        subscriptionService.updateSubscription(uuid, subscription);
        return "redirect:/api/admin/subscription/subscriptions";
    }

    @GetMapping("/delete/{uuid}")
    public String deleteSubscriptionByUuid(@PathVariable String uuid) {
        subscriptionService.deleteSubscription(uuid);
        return "redirect:/api/admin/subscription/subscriptions";
    }
}
