package org.webtree.trust.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.webtree.trust.common.utils.TimeProvider;
import org.webtree.trust.domain.TrustUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by stephan on 10.09.16.
 */
public class JwtTokenUtilTest {

    private static final String TEST_USERNAME = "testUser";
    private static final String USER_ID ="someUserId";

    @Mock
    private TimeProvider timeProviderMock;

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 3600L); // one hour
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", "mySecret");
    }

    @Test
    public void testGenerateTokenGeneratesDifferentTokensForDifferentCreationDates() {
        when(timeProviderMock.now())
            .thenReturn(DateUtil.yesterday())
            .thenReturn(DateUtil.now());

        final String token = createToken();
        final String laterToken = createToken();

        assertThat(token).isNotEqualTo(laterToken);
    }

    @Test
    public void getUsernameFromToken() {
        when(timeProviderMock.now()).thenReturn(DateUtil.now());

        final String token = createToken();

        assertThat(jwtTokenUtil.getUsernameFromToken(token)).isEqualTo(TEST_USERNAME);
    }


    @Test
    public void getCreatedDateFromToken() {
        final Date now = DateUtil.now();
        when(timeProviderMock.now()).thenReturn(now);

        final String token = createToken();

        assertThat(jwtTokenUtil.getIssuedAtDateFromToken(token)).isInSameMinuteWindowAs(now);
    }

    @Test
    public void getExpirationDateFromToken() {
        final Date now = DateUtil.now();
        when(timeProviderMock.now()).thenReturn(now);
        final String token = createToken();

        final Date expirationDateFromToken = jwtTokenUtil.getExpirationDateFromToken(token);
        assertThat(DateUtil.timeDifference(expirationDateFromToken, now)).isCloseTo(3600000L, within(1000L));
    }

  /*  @Test
    public void getAudienceFromToken() throws exception {
        when(timeProviderMock.now()).thenReturn(DateUtil.now());
        final String token = createToken();

        assertThat(jwtTokenUtil.getAudienceFromToken(token)).isEqualTo(JwtTokenUtil.AUDIENCE_WEB);
    }*/

    @Test(expected = ExpiredJwtException.class)
    public void expiredTokenCannotBeRefreshed() {
        when(timeProviderMock.now())
            .thenReturn(DateUtil.yesterday());
        String token = createToken();
        jwtTokenUtil.canTokenBeRefreshed(token, DateUtil.tomorrow());
    }

    @Test
    public void changedPasswordCannotBeRefreshed() {
        when(timeProviderMock.now())
            .thenReturn(DateUtil.now());
        String token = createToken();
        assertThat(jwtTokenUtil.canTokenBeRefreshed(token, DateUtil.tomorrow())).isFalse();
    }

    @Test
    public void notExpiredCanBeRefreshed() {
        when(timeProviderMock.now())
            .thenReturn(DateUtil.now());
        String token = createToken();
        assertThat(jwtTokenUtil.canTokenBeRefreshed(token, DateUtil.yesterday())).isTrue();
    }

    @Test
    public void canRefreshToken() {
        when(timeProviderMock.now())
            .thenReturn(DateUtil.now())
            .thenReturn(DateUtil.tomorrow());
        String firstToken = createToken();
        String refreshedToken = jwtTokenUtil.refreshToken(firstToken);
        Date firstTokenDate = jwtTokenUtil.getIssuedAtDateFromToken(firstToken);
        Date refreshedTokenDate = jwtTokenUtil.getIssuedAtDateFromToken(refreshedToken);
        assertThat(firstTokenDate).isBefore(refreshedTokenDate);
    }

    @Test
    public void canValidateToken() {
        when(timeProviderMock.now())
            .thenReturn(DateUtil.now());
        TrustUser trustUserDetails = mock(TrustUser.class);
        when(trustUserDetails.getUsername()).thenReturn(TEST_USERNAME);

        String token = createToken();
        assertThat(jwtTokenUtil.validateToken(token, trustUserDetails)).isTrue();
    }

    private Map<String, Object> createClaims(String creationDate) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtTokenUtil.CLAIM_KEY_USERNAME, TEST_USERNAME);
        claims.put(JwtTokenUtil.CLAIM_KEY_AUDIENCE, "testAudience");
        claims.put(JwtTokenUtil.CLAIM_KEY_CREATED, DateUtil.parseDatetime(creationDate));
        return claims;
    }

    private String createToken() {
        return jwtTokenUtil.generateToken(TrustUser.builder().username(TEST_USERNAME).id(USER_ID).build());
    }

}