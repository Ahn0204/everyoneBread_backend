package com.eob.shop.controller;

import java.io.File;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.eob.member.model.dto.RegisterRequest;
import com.eob.member.model.data.MemberEntity;
import com.eob.member.service.MemberService;
import com.eob.shop.model.data.ShopEntity;
import com.eob.shop.service.ShopService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/*")
public class ShopController {

    private final MemberService memberService; 
    private final ShopService shopService;     

    /*
       판매자 로그인 페이지
    */
    @GetMapping("login")
    public String shopLogin() {
        return "shop/shop-login";
    }

    /*
        판매자 회원가입 페이지
    */
    @GetMapping("register/start")
    public String registerStartForm(Model model) {

        model.addAttribute("registerRequest", new RegisterRequest());
        return "shop/shop-register-start";
    }

    /*
        판매자 회원 저장
    */
    @PostMapping("register/start")
    public String registerStart(@Valid @ModelAttribute("registerRequest") RegisterRequest dto, BindingResult bindingResult, Model model, HttpSession session) {

        // Valid 검증 실패
        if (bindingResult.hasErrors()) {
            return "shop/shop-register-start";
        }

        // ROLE 강제 SHOP 주입
        dto.setMemberRole("SHOP");

        // DB에 저장하지 않고 세션에 임시 저장
        session.setAttribute("tempShopMember", dto);

        return "shop/shop-register-step";
    }

    /*
        상점 정보 입력 화면
    */
    @GetMapping("register/step")
    public String registerStepForm(Model model, HttpSession session) {

        RegisterRequest temp = (RegisterRequest) session.getAttribute("tempShopMember");
        if(temp == null){
            return "redirect:/shop/register/start";
        }

        return "shop/shop-register-step";
    }

    /*
        상점 정보 저장
    */
    @PostMapping("register/step")
    public String registerStep(ShopEntity shop,HttpSession session, @RequestParam("bizFile") MultipartFile bizFile) throws Exception {

        // 세션에 담아둔 회원 정보 가져오기
        RegisterRequest temp = (RegisterRequest) session.getAttribute("tempShopMember");
        if(temp == null){
            return "redirect:/shop/register/start";
        }
        // 회원 저장
        MemberEntity member = memberService.registerShop(temp,null);

        // 파일 저장
        String fileName = null;
        if(!bizFile.isEmpty()){
            fileName = System.currentTimeMillis() + "_" + bizFile.getOriginalFilename();
            String savePath = "C:/upload/shop/" + fileName;
            bizFile.transferTo(new File(savePath));
        }

        // ShopEntity 세팅
        shop.setMember(member);
        shop.setSellerName(member.getMemberName());
        shop.setCreatedAt(LocalDateTime.now());
        shop.setBizImg(fileName);
        shopService.saveShop(shop);

        // 세션 삭제
        session.removeAttribute("tempShopMember");

        return "redirect:/shop";
    }

    /*
        상점명 중복 확인
    */
    @GetMapping("check-name")
    @ResponseBody
    public boolean checkShopName(@RequestParam("shopName") String shopName) {
        return !shopService.existsByShopName(shopName);
    }

    /*
        판매자 메인 페이지
    */
    @GetMapping("")
    public String shopMain() {
        return "shop/shop-main";
    }
}
