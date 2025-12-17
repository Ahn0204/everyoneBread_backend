package com.eob.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import lombok.RequiredArgsConstructor;

//설정을 담당하는 어노테이션 
@Configuration
// Security 설정 활성화
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomDetailService customDetailService;

        private final CustomAuthFailureHandler customAuthFailureHandler;

        private final CustomLoginSuccessHandler customLoginSuccessHandler;

        // CustomAuthenticationProvider 를 Bean 으로 등록
        // -
        @Bean
        public CustomAuthenticationProvider customAuthenticationProvider() {
                return new CustomAuthenticationProvider(customDetailService, passwordEncoder());
        }

        @Bean
        @Order(1)
        SecurityFilterChain riderFilterChain(HttpSecurity http) throws Exception {
                // 성진 : 콘솔로그 확인용 추가
                System.out.println("[RIDER CHAIN] LOADED");
                // securityMatcher("/**") : / 경로와 그 하위 경로에만 적용되도록 범위를 지정
                // authorizeHttpRequests() : 요청 URL에 대한 접근 권한 규칙을 정의
                // anyRequest().permitAll(s) : 현재 체인의 매칭 범위 안에 포함된 모든 요청을 인증/인가 절차 없이 허용
                http
                                // Security의 보안이 적용되는 URL 경로를 지정해주는 설정
                                // 이 체인은 http://localhost:8080/rider 하위의 모든 경로에 적용
                                // - "/rider/*" : /rider 바로 하위 경로(/rider/login,/rider/order)에만 설정 적용 하위의 하위
                                // 경로(/rider/order/list 등)는 적용되지 않음
                                // - "/rider/**"
                                .securityMatcher("/rider/**")

                                // Security에서 "로그인 ID/PW"가 맞는지 실제로 확인하는 엔진
                                // authenticationProvider(...)
                                // - 사용자가 로그인 요청 시 입력한 ID/PW가 맞는지 검사하는 핵심 인증 처리 로직.
                                // - 내부적으로 UserDetailsService를 통해 DB에서 사용자 정보를 조회하고, PasswordEncoder를 사용하여 비밀번호
                                // 일치여부 조회.
                                // - 또한 계정의 상태(활성/정지/탈퇴 등) 까지 해당 요청에서 검증하고, 인증에 실패하면 FailureHandler로 이동한다.
                                .authenticationProvider(customAuthenticationProvider())

                                // Security의 요청(URL)에 대한 접근 권한(Authorization) 설정
                                // authrizeHttpRequests(...)
                                // - 요청별(URL별) 보안 규칙을 정의하는 메서드이다.
                                // - 어떤 경로는 로그인 없이 접근 가능(permitAll)하고,어떤 경로는 로그인해야 접근 가능(authenticated)인지 등을
                                // 설정한다.
                                // - 내부에 HttpSecurity가 요청을 필터링할 때, 요청이 들어오면 먼저 이 규칙과 비교해 접근 권한을 판별한다.
                                .authorizeHttpRequests(auth -> auth

                                                // 인증 불필요한 페이지 지정
                                                // .requestMatchers("/rider/login").permitAll()
                                                // - /rider/login 경로에 대해서는 누구나 접근 허용(인증 불필요)하도록 설정
                                                // - 정적리소스(css,js,image,fonts,lib 등) 정적인 리소스는 접근 가능하도록 설정
                                                // 로그인 페이지에 접근하기 위해서는 로그인하지 않아도 접근(접속)할 수 있도록 설정하는 것.
                                                // - permitAll()은 Security가 내부적으로 익명의 사용자(AnonymousUser)도 접근 허용하게 만들어준다.
                                                .requestMatchers("/rider/login", "/rider/register/*",
                                                                "/rider/revision-request/*", "/css/**",
                                                                "/js/**", "/image/**",
                                                                "/fonts/**", "/lib/**")
                                                .permitAll()

                                                // 위에 명시하지 않은 나머지 요청에 대한 인증설정
                                                // .anyRequest().permitAll()
                                                // - requestMatchers(...) 로 경로를 지정하지 않은 다른 요청들에 대한 접근여부를 확인한다.
                                                // - authenticated()은 Security가 내부적으로 인증(로그인)된 사용자만 접근할 수 있도록 허용해주는 메서드
                                                // 만약 대부분의 기능이 공개되어 있고, 일부만 로그인이 필요한 경우에는
                                                // @EnableMethodSecurity(prePostEnabled = true) 로 설정 후
                                                // 컨트롤러 단에서 @PreAuthorize("isAuthenticated()")로 로그인 여부를 제어할 수 있다.
                                                // .anyRequest().permitAll())
                                                .anyRequest().hasRole("RIDER"))

                                // Security의 로그인(formLogin) 인증 설정의 핵심 설정
                                // 사용자가 로그인 폼을 제출할 때 어떤 URL로 보내고, 어떤 파라미터의 이름을 사용하고, 성공/실패 시 동작 여부를 정의하는 영역
                                // formLogin(login -> loing ...)
                                // - 폼 기반 로그인(Form-based authentication)을 사용하겠다는 설정
                                // - 즉 사용자가 로그인 페이지에서 <form>을 제출하면 UsernamePasswordAuthenticationFilter 가 요청을
                                // 가로채서 인증을 수행한다.
                                .formLogin(login -> login
                                                // .loginPage("/rider/login")
                                                // - 로그인 페이지 URL (GET 전용) 이다.
                                                // - 로그인 화면이 표시되는 GET 요청 경로를 지정하는 곳이다.
                                                // - 반드시 컨트롤러에서 "/rider/login" GET 매핑이 있어야 하며, 해당 URL은 반드시 permitAll,
                                                // 요청이 허용되어 있어야한다.
                                                // - 사용자가 로그인되지 않은 상태로 인증이 필요한 페이지에 접근하면, Security가 자동으로 해당 URL로 리다이렉트
                                                // 시킨다.
                                                .loginPage("/rider/login")

                                                // .loginProcessingUrl("/rider/login")
                                                // - 로그인 처리 URL (POST 전용) 이다.
                                                // - 사용자가 form을 제출할 때 POST 요청을 보낼 URL을 지정하는 곳이다.
                                                // - 해당 요청이 오면 Security의 내부 필터(UsernamePasswordAuthenticationFilter)가
                                                // 가로채어 실제 로그인 검증을 수행한다.
                                                // - 보통 별도의 @PostMapping 컨트롤러를 만들지 않는다(만들면 충돌 위험).
                                                // - 반드시 로그인 폼의 action 과 정확히 일치해야 하며,
                                                // - 인증이 필요한 경로이지만, 로그인 전 접근이 가능해야 하므로 permitAll() 로 허용되어야 한다.
                                                // - 기본값은 "/login"이며, 필요 시 경로를 커스텀할 수 있다.
                                                .loginProcessingUrl("/rider/login")

                                                // .usernameParameter()/.passwordParameter()
                                                // - 로그인 폼의 아이디/비밀번호 파라미터명 매핑
                                                // - 로그인 폼의 <input name="..."> 와 동일해야 합니다.
                                                // - 기본값은 username/password 이지만, 사용자의 커스텀 이름(riderId/riderPw)을 사용할 경우
                                                // Security가 해당 파라미터를 찾을 수있게 사용한.
                                                .usernameParameter("riderId")
                                                .passwordParameter("riderPw")

                                                // .successHandler()
                                                // - 로그인 성공 후의 동작을 직접 정의할 수 있다.
                                                // SavedRequestAwareAuthenticationSuccessHandler 인터페이스를 구현한 커스텀 클래스로 해당
                                                // 로직을 정의한다.
                                                // - 특정 페이지로 리다이렉트
                                                // - 회원 상태(정지,탈퇴 등)에 따른 접근 제한
                                                // - 세션 데이터 저장,로그 기록 등
                                                .successHandler(customLoginSuccessHandler)

                                                // .failureHandler()
                                                // 로그인 실패 시의 동작을 직접 정의할 수 있다.
                                                // AuthenticationFailureHandler 인터페이스를 구현한 커스텀 클래스로 해당 로직을 정의한다.
                                                // - 아이디/비밀번호 오류
                                                // - 계정 비활성화 (isEnabled() == false)
                                                // - 비밀번호 5회 이상 오류시 계정 장금처리 등
                                                .failureHandler(customAuthFailureHandler)

                                // [선택] 로그인 페이지/처리 URL 모두 익명 접근 허용(편의 메서드)
                                // - 이미 authorizeHttpRequests 에서 permitAll 로 열어두었다면 생략 가능.
                                // .permitAll()
                                )
                                .userDetailsService(customDetailService)

                                // .logout()
                                // - 로그아웃에 관한 설정
                                .logout(logout -> logout
                                                // .logoutRequestMathcer()
                                                // 로그아웃을 요청하는 URL과 HTTP 메서드를 설정
                                                // Security이 로그아웃 요청을 POST "/logout" 이
                                                // GET /rider/logout 허용
                                                // 실제 운영되는 서버에서는 GET 매핑이 아닌 POST 매핑으로 설정해주어야한다.
                                                .logoutRequestMatcher(
                                                                new AntPathRequestMatcher("/rider/logout", "POST"))

                                                // .logoutSuccessUrl()
                                                // 로그아웃을 성공 한 후 이동할 페이지 지정하는 설정
                                                // - 만약 로그인 페이지로 이동시키고 싶다면 .logoutSuccessUrl("/rider/login?logout") 이렇게
                                                // 바꿔도 된다.
                                                .logoutSuccessUrl("/rider/login")

                                                // .invalidateHttpSession()
                                                // 로그아웃 시 서버 세션을 무효화하는 설정
                                                // - HttpSession 객체를 완전히 제거하여 기존 로그인 정보,인증 토큰,사용자 데이터 등이 모두 사라진다.
                                                // - 세션 무효화를 하지 않으면 이전 로그인 정보가 남을 수 있으므로 거의 항상 true로 설정한다.
                                                .invalidateHttpSession(true)

                                                // .deleteCookies()
                                                // 로그아웃 시 클라이언트(브라우저)에 저장된 JSESSIONID 쿠키를 삭제한다.
                                                // 해당 쿠키는 세션 식별자 역할을 하므로, 삭제하면 재로그인 없이 이전 세션을 사용할 수 없게 된다.
                                                // 보통 invalidateHttpSession(true) 와 함께 사용해서 완전히 클린 로그아웃 상태로 만든다.
                                                .deleteCookies("JSESSIONID"))

                                // .csrf()
                                // CSRF(Cross-Site Request Forgery)
                                // 사용자가 로그인 된 상태에서 악의적인 사이트가 몰래 요청을 보내 유도하는 공격
                                .csrf(csrf -> csrf
                                                // .ignoringRequestMatchers("")
                                                // .ignoringRequestMatchers("/rider/logout")
                                                // csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                                                // CSRF 토큰을 서버 세션(HttpSession)에 저장하도록 설정한다.
                                                // - 폼이나 AJAX 요청을 보낼 때 마다 CSRF 토큰을 자동적으로 검증한다.
                                                // - 기본적으로 POST,PUT,DELETE 요청에 적용되며 클라이언트가 보낸 토큰과 세션에 저장된 토큰이 일치해야 요청이
                                                // 통과된다.
                                                // - 개발 중 임시로 CSRF를 끄는경우도 있으나 로그인,회원가입 과 같은 POST요청이 제대로 작동되지 않을 수 있으므로
                                                // 테스트 후 복원해야한다.
                                                // .csrf(csrf -> csrf.disable));
                                                .csrfTokenRepository(new HttpSessionCsrfTokenRepository()));

                // http.build()
                // Security의 설정 클래스(SecurityConfig)의 마지막 핵심 마무리 코드
                // - HttpSecurity로 작성한 모든 보안 설정을 SecurityFilterChain 객체로 빌드해서 반환하는 부분
                // - spring은 내부적으로 FilterRegistrationBean<DelegatingFilterProxy> 를 등록해서, 모든 요청을
                // SecurityFilterChain에 연결합니다.
                // - 즉 지금까지 정의한 시큐리티 설정을 완성해 애플리케이션에 적용하라는 명령문이다.
                return http.build();
        }

        // 예솔: Order가 default랑 똑같이 4였어서 2로 바꿨습니다.
        @Bean
        @Order(2)
        SecurityFilterChain shopFilterChain(HttpSecurity http) throws Exception {
                // 성진 : 콘솔로그 확인용
                System.out.println("[SHOP CHAIN] LOADED");
                http.addFilterBefore((request, response, chain) -> {
                        System.out.println("🟦 [SHOP CHAIN ACTIVE] → " + request.getRequestId());
                        chain.doFilter(request, response);
                }, org.springframework.web.filter.CorsFilter.class);

                http

                                /*
                                 * 이 필터체인이 적용될 URL 패턴 지정
                                 * /shop/** 로 시작하는 모든 URL은 여기에서 처리됨
                                 */
                                .securityMatcher("/shop/**")

                                .authenticationProvider(customAuthenticationProvider())

                                .authorizeHttpRequests(auth -> auth
                                                /*
                                                 * permitAll() : 로그인하지 않아도 접근 허용
                                                 * shop-register(입점 신청) GET/POST 모두 허용
                                                 * shop 로그인 페이지도 허용
                                                 * 정적 리소스(css/js/img/...) 허용
                                                 */
                                                .requestMatchers(
                                                                "/shop/register/start", // 가입 폼 GET
                                                                "/shop/register/start/**", // 가입 처리 POST
                                                                "/shop/register/step",
                                                                "/shop/register/step/**",
                                                                "/shop/login", // 판매자 로그인 페이지
                                                                "/shop/check-id",
                                                                "/shop/check-email",
                                                                "/shop/check-name",
                                                                "/shop/orders/dashboard", // 주문 대시보드 AJAX
                                                                "/css/**", "/js/**", "/image/**", "/fonts/**",
                                                                "/lib/**")
                                                .permitAll()

                                                /*
                                                 * /shop/** 내부의 모든 요청은 판매자(SHOP)권한만 접근 가능
                                                 * /shop/** 경로는 오직 ROLE_SHOP 계정만 접근할 수 있도록 제한한다.
                                                 */
                                                .anyRequest().hasRole("SHOP"))

                                /*
                                 * 판매자(Shop) 로그인 설정
                                 * - loginPage() : 로그인 화면을 보여줄 GET URL
                                 * - loginProcessingUrl() : 로그인 POST 요청 처리 URL
                                 * - usernameParameter / passwordParameter : form input name 설정
                                 */
                                .formLogin(login -> login
                                                .loginPage("/shop/login") // 로그인 페이지 (GET)
                                                .loginProcessingUrl("/shop/login") // 로그인 처리 (POST)
                                                .usernameParameter("shopId") // input name="shopId"
                                                .passwordParameter("shopPw") // input name="shopPw"
                                                .successHandler(customLoginSuccessHandler) // 로그인 성공 시 처리
                                                .failureHandler(customAuthFailureHandler) // 로그인 실패 시 처리
                                )
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/shop/logout", "GET"))
                                                .logoutSuccessUrl("/shop/login")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))

                                /* 개발 단계에서 CSRF 비활성화 */
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }

        @Bean
        @Order(3)
        SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
                // 성진 : 콘솔로그 확인용 추가
                System.out.println("[ADMIN CHAIN] LOADED");
                http
                                // url이 /admin/~인 요청에 이 필터체인 적용
                                .securityMatcher("/admin/**")
                                .authorizeHttpRequests((auth) -> auth
                                                // 관리자 로그인 페이지의 모든 사용자 접근 허용
                                                .requestMatchers("/admin/login", "/admin/logout", "/css/**", "/js/**",
                                                                "/image/**",
                                                                "/fonts/**", "/lib/**")
                                                .permitAll()
                                                // .anyRequest().permitAll())
                                                // 이외 모든 경로 관리자만 접근 허용
                                                .anyRequest().hasRole("ADMIN"))

                                // 관리자 로그인 페이지 설정&처리
                                .formLogin((auth) -> auth
                                                // 관리자 로그인 페이지 설정
                                                .loginPage("/admin/login")
                                                // 로그인 처리 url 로그인폼의 actiion=경로와 일치해야함
                                                .loginProcessingUrl("/admin/login")
                                                // username파라미터의 이름 >> userDetailService 내 메소드(인자)의 파라미터명을 지정하는 것임.
                                                .usernameParameter("adminId")
                                                // password파라미터의 이름
                                                .passwordParameter("adminPw")
                                                // 로그인 성공시의 동작 정의(핸들러)
                                                .successHandler(customLoginSuccessHandler)
                                                // 로그인 성공 시 리다이렉트 될 url
                                                // .defaultSuccessUrl("/admin", true) // 항상 이 url사용함(강제이동)
                                                // 로그인 실패시의 동작 정의(핸들러)
                                                .failureHandler(customAuthFailureHandler)
                                                // 로그인 실패 시 리다이렉트 될 url
                                                // .failureUrl("/admin/login?error=true")
                                                .permitAll())

                                // 로그아웃 요청
                                .logout((auth) -> auth
                                                // 로그아웃 요청 url, http메서드 설정
                                                // POST매핑으로 해놓아야 사용자가 URL로 접속할 수 없음
                                                // 꼭 대문자로 써야함..
                                                // .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
                                                .logoutRequestMatcher(
                                                                new AntPathRequestMatcher("/admin/logout", "POST"))
                                                // 로그아웃 성공 시 url
                                                .logoutSuccessUrl("/admin/")
                                                // 로그아웃 시 세션 무효화
                                                .invalidateHttpSession(true)
                                                // 로그아웃 시 브라우저에 저장된 JSESSIONID 쿠키 삭제
                                                .deleteCookies("JSESSIONID"))

                                // csrf비활성화
                                // .csrf(csrf -> csrf.disable()
                                .csrf(csrf -> csrf
                                                // csrf 활성화
                                                .csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
                                // .csrfTokenRepository(CookieCsrfTokenRepository)와의 차이는?

                                // 검증 시 사용하는 객체 지정
                                // ROLE값이 로그인 페이지에 맞는지 확인 => 비밀번호 맞는지 확인 => (필요하다면 승인여부 확인)
                                .authenticationProvider(customAuthenticationProvider())

                                // 이 경로에서 사용할 userDetailsService 설정(@Bean으로 등록된 UserDetails가 여러개일 경우 필수)
                                .userDetailsService(customDetailService);

                return http.build();

        }

        @Bean
        // 여러 체인이 있을 때 우선순위를 지정하는 어노테이션, 숫자가 낮을수록 우선순위가 높음
        @Order(4)
        SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
                // 성진 : 콘솔로그 확인용 추가
                System.out.println("[DEFAULT CHAIN] LOADED");
                http
                                /**
                                 * Security가 적용될 URL 범위 지정
                                 * -> 이 체인은 /member/**, /, /main, /css/**, /js/**, /images/** 경로에 적용
                                 * -> 즉, 회원 관련 기능과 메인 페이지, 정적 리소스에 대한 보안 설정을 담당
                                 */

                                .securityMatcher("/member/**", "/**")
                                // .securityMatcher("/member/**", "/**")

                                // 예솔: 메인 페이지에서는 securityMatcher를 안쓰려고 하는데 어떤가요
                                // /**이라고 경로를 지정하는게 보안에 의미가 없고,
                                // 위에서 체인에 걸리지 않은 url은 다 여기로 오게된다고 g가 그랬어요.
                                // .securityMatcher("/member/**", "/", "/main", "/css/**", "/js/**",
                                // "/image/**")
                                /**
                                 * 성진
                                 * securityMatcher 없는 체인은 모든 URL을 대상으로 하기 때문에
                                 * 특정 URL이 예상치 못하게 막히거나 리다이렉트가 발생함
                                 * 
                                 * 모든 FilterChain에 대해 securityMatcher를 명시적으로 지정하는 것이 좋음
                                 * 그러지 않으면 defaultChain이 전체 URL 보안을 가져가서 대부분의 요청이 로그인으로 튕긴다.
                                 * 
                                 * 2025/12/04
                                 * 회원가입 - 일반/비즈니스 계정 선택 시 지속적으로 로그인 페이지로 튕기는 문제 발생
                                 */

                                /*
                                 * 병주
                                 * 시큐리티 에 "/**" 해당 매처가 없으면 sec:authorize를 사용 못해요..
                                 * 그래서 해당 securityMatcher가 있어야 함니다..
                                 */

                                .authenticationProvider(customAuthenticationProvider())

                                /**
                                 * URL 접근 허용 설정
                                 */
                                .authorizeHttpRequests(auth -> auth
                                                /* 인증 없이 접근 가능한 요청 목록 */
                                                .requestMatchers(HttpMethod.GET, "/member/login").permitAll() // 로그인
                                                                                                              // 페이지(GET)
                                                .requestMatchers( // 예솔: 메인 홈페이지에서 비회원도 접근가능한 링크 추가했습니다.
                                                                "/", // 메인 페이지
                                                                "/getCategory",
                                                                "/shopList/**", // 상점 목록 페이지
                                                                "/shopList/detail", // 상품 선택페이지
                                                                "/shopList/detail/**", // 상품 선택페이지
                                                                "/member/register", // 회원가입 페이지(GET/POST)
                                                                "/member/register/**",
                                                                "/member/select", // 계정 유형 선택 페이지(GET)
                                                                "/member/check-id", // 아이디 중복 체크 AJAX
                                                                "/member/check-email", // 이메일 중복 체크 AJAX
                                                                // "/member/send-auth-code", // 문자 전송 AJAX
                                                                // "/member/verify-auth-code", // 문자 인증코드 확인 AJAX
                                                                "/customerCenter", // 고객센터
                                                                "/customerCenter/**",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/image/**",
                                                                "/fonts/**",
                                                                "/lib/**",
                                                                "/shop/**")
                                                .permitAll()

                                                /* 위에서 허용한 URL 외 모든 요청은 로그인 필요 */
                                                .anyRequest().permitAll())
                                // 예솔: role='user'인 사용자만 로그인되도록 나중에 변경
                                // .anyRequest().hasRole("USER"))

                                /* 로그인 설정 */
                                .formLogin(login -> login
                                                /* 로그인 페이지(GET) 경로 지정 */
                                                .loginPage("/member/login")
                                                /* 로그인 요청을 처리하는 URL (POST) */
                                                .loginProcessingUrl("/member/login")
                                                /* form에서 사용하는 input name 지정 */
                                                .usernameParameter("memberId")
                                                .passwordParameter("memberPw")
                                                /* 로그인 성공/실패 시 커스텀 핸들러 호출 */
                                                .successHandler(customLoginSuccessHandler)
                                                .failureHandler(customAuthFailureHandler))

                                // /* 자동 로그인 remember-me */
                                // .rememberMe(remember -> remember
                                // /**
                                // * remember-me 기능의 암호화 key
                                // * - 이 값이 바뀌면 기존 remember-me 쿠키는 모두 무효가 된다.
                                // * - 프로젝트 고유 문자열을 넣어야 하며 외부에 노출되면 안 된다.
                                // */
                                // .key("everyoneBreadRememberKey")
                                // /**
                                // * 사용자가 체크박스를 선택했을 때 전달되는 파라미터 이름
                                // * - 로그인 폼 input name="autoLogin" 과 반드시 일치해야 한다.
                                // * - 체크 시 "autoLogin=on" 값이 서버로 넘어와 remember-me가 활성화된다.
                                // */
                                // .rememberMeParameter("autoLogin")
                                // /**
                                // * 자동 로그인 유지 기간 설정 (초 단위)
                                // * - 60초 * 60분 * 24시간 * 30일 = 30일 동안 로그인 유지
                                // * - 기간 내 브라우저를 껐다 켜도 다시 자동 로그인 됨
                                // */
                                // .tokenValiditySeconds(60 * 60 * 24 * 30)
                                // /**
                                // * remember-me 토큰으로 자동 로그인할 때
                                // * 사용자 정보를 불러올 customUserDetailsService 지정
                                // * - 이 서비스가 DB에서 회원 정보를 조회하여 인증을 복원한다.
                                // * - 반드시 설정해야 remember-me가 정상 작동한다.
                                // */
                                // .userDetailsService(customDetailService))

                                /* 로그아웃 설정 */
                                .logout(logout -> logout
                                                /* 로그아웃 요청 URL (GET) */
                                                .logoutRequestMatcher(
                                                                new AntPathRequestMatcher("/member/logout", "GET"))
                                                /* 로그아웃 성공 시 이동할 URL */
                                                .logoutSuccessUrl("/")
                                                /* 세션 완전 삭제 */
                                                .invalidateHttpSession(true)
                                                /* JSESSIONID 쿠키 삭제 및 자동 로그인 쿠키 삭제 */
                                                .deleteCookies("JSESSIONID", "remember-me"))

                                /**
                                 * CSRF 설정
                                 * - 개발 중에는 disable()로 비활성화할 수 있으나, 실제 운영 시에는 반드시 복원 필요
                                 * - 정식 오픈 전 반드시 다시 켜야 함.
                                 * - 테스트 동안만 CSRF 비활성화
                                 * - 테스트 후 아래 코드 주석 해제 필요
                                 * .csrfTokenRepository(new HttpSessionCsrfTokenRepository()));
                                 */
                                // 예솔: csrf토큰 기능을 활성화 했습니다.
                                // .csrf(csrf -> csrf.disable())
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
                                .userDetailsService(customDetailService);
                return http.build();
        }

        // 비밀번호 인코딩(암호화) BCryptPasswordEncoder 를 사용하여 복호화되지 않는 값으로 암호화 처리해 준다.
        // - BCrypt는 솔트(salt)를 내부에서 자동 생성하여 해시에 포함한다.
        // member.setMemberPw(passwordEncoder.encode(memberRegisterForm.getMemberPw()));
        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
