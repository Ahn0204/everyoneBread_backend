package com.eob.member.controller;

import com.eob.member.model.dto.AddressRequest;
import com.eob.member.service.AddressBookService;
import com.eob.common.security.CustomSecurityDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/mypage/delivery")
public class AddressBookController {

    private final AddressBookService addressBookService;

    /* 배송지 목록 */
    @GetMapping
    public String list(Model model,
                       Authentication authentication) {

        CustomSecurityDetail user =
            (CustomSecurityDetail) authentication.getPrincipal();

        model.addAttribute(
            "addressList",
            addressBookService.getActiveAddresses(user.getMember())
        );

        return "member/mypage/deliveryList";
    }

    /* 배송지 추가 */
    @PostMapping("/add")
    public String add(AddressRequest request,
                      Authentication authentication) {

        CustomSecurityDetail user =
            (CustomSecurityDetail) authentication.getPrincipal();

        addressBookService.create(
            user.getMember(), request
        );

        return "redirect:/member/mypage/delivery";
    }

    /* 배송지 삭제 (논리 삭제) */
    @PostMapping("/delete/{addressNo}")
    public String delete(@PathVariable Long addressNo) {

        addressBookService.delete(addressNo);

        return "redirect:/member/mypage/delivery";
    }
}
