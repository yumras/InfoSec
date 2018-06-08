package org.owasp.webgoat.plugin.challenge2;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.owasp.webgoat.plugin.SolutionConstants.SUPER_COUPON_CODE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author nbaars
 * @since 5/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShopEndpointTest {

    private MockMvc mockMvc;

    @Before
    public void setup() {
        ShopEndpoint shopEndpoint = new ShopEndpoint();
        this.mockMvc = standaloneSetup(shopEndpoint).build();
    }

    @Test
    public void getSuperCoupon() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/challenge-store/coupons/" + SUPER_COUPON_CODE))
                .andExpect(jsonPath("$.code", CoreMatchers.is(SUPER_COUPON_CODE)))
                .andExpect(jsonPath("$.discount", CoreMatchers.is(100)));
    }

    @Test
    public void getCoupon() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/challenge-store/coupons/webgoat"))
                .andExpect(jsonPath("$.code", CoreMatchers.is("webgoat")))
                .andExpect(jsonPath("$.discount", CoreMatchers.is(25)));
    }

    @Test
    public void askForUnknownCouponCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/challenge-store/coupons/does-not-exists"))
                .andExpect(jsonPath("$.code", CoreMatchers.is("no")))
                .andExpect(jsonPath("$.discount", CoreMatchers.is(0)));
    }

    @Test
    public void fetchAllTheCouponsShouldContainGetItForFree() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/challenge-store/coupons/"))
                .andExpect(jsonPath("$.codes[3].code", is("get_it_for_free")));
    }

}