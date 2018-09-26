package org.webtree.trust.social;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.webtree.trust.exception.ProviderNotSupportedException;
import org.mockito.Mock;
import org.webtree.trust.service.social.FacebookService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Udjin Skobelev on 13.08.2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class SocialServicesProviderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Mock
    private FacebookService service;

    private SocialServicesProvider holder;

    @Before
    public void setUp() {
        holder = new SocialServicesProvider();
    }

    @Test
    public void shouldAddAndGetService() {
        String providerId = "facebook";
        holder.addService(providerId, service);
        assertThat(holder.getService(providerId)).isEqualTo(service);
    }

    @Test
    public void shouldThrowExceptionIfDoesNotExist() {
        exception.expect(ProviderNotSupportedException.class);
        holder.getService("someRandomName");
    }
}