package org.owasp.webgoat.plugin.challenge2;

import com.beust.jcommander.internal.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.owasp.webgoat.plugin.SolutionConstants.SUPER_COUPON_CODE;

/**
 * @author nbaars
 * @since 4/6/17.
 */
@RestController
@RequestMapping("challenge-store")
public class ShopEndpoint {

    @AllArgsConstructor
    private class CheckoutCodes {

        @Getter
        private List<CheckoutCode> codes = Lists.newArrayList();

        public Optional<CheckoutCode> get(String code) {
            return codes.stream().filter(c -> c.getCode().equals(code)).findFirst();
        }
    }

    @AllArgsConstructor
    @Getter
    private class CheckoutCode {
        private String code;
        private int discount;
    }

    private CheckoutCodes checkoutCodes;

    public ShopEndpoint() {
        List<CheckoutCode> codes = Lists.newArrayList();
        codes.add(new CheckoutCode("webgoat", 25));
        codes.add(new CheckoutCode("owasp", 25));
        codes.add(new CheckoutCode("owasp-webgoat", 50));
        this.checkoutCodes = new CheckoutCodes(codes);
    }

    @GetMapping(value = "/coupons/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CheckoutCode getDiscountCode(@PathVariable String code) {
        if (SUPER_COUPON_CODE.equals(code)) {
            return new CheckoutCode(SUPER_COUPON_CODE, 100);
        }
        return checkoutCodes.get(code).orElse(new CheckoutCode("no", 0));
    }

    @GetMapping(value = "/coupons", produces = MediaType.APPLICATION_JSON_VALUE)
    public CheckoutCodes all() {
        List<CheckoutCode> all = Lists.newArrayList();
        all.addAll(this.checkoutCodes.getCodes());
        all.add(new CheckoutCode(SUPER_COUPON_CODE, 100));
        return new CheckoutCodes(all);
    }
}
