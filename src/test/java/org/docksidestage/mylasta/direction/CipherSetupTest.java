package org.docksidestage.mylasta.direction;

import jakarta.annotation.Resource;

import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.core.security.PrimaryCipher;
import org.lastaflute.web.servlet.cookie.CookieCipher;

/**
 * @author jflute
 */
public class CipherSetupTest extends UnitHarborTestCase {

    @Resource
    private PrimaryCipher primaryCipher;
    @Resource
    private CookieCipher cookieCipher;

    public void test_primary() throws Exception {
        String encrypted = primaryCipher.encrypt("sea");
        log("encrypted: {}", encrypted);
        String decrypted = primaryCipher.decrypt(encrypted);
        log("decrypted: {}", decrypted);
        assertEquals("sea", decrypted);
        log(primaryCipher.oneway("land")); // expects no exception
    }

    public void test_cookie() throws Exception {
        String encrypted = cookieCipher.encrypt("sea");
        log("encrypted: {}", encrypted);
        String decrypted = cookieCipher.decrypt(encrypted);
        log("decrypted: {}", decrypted);
        assertEquals("sea", decrypted);
    }
}
