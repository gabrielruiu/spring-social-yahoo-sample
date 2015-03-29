/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.quickstart.config;

import com.github.gabrielruiu.springsocial.yahoo.api.Yahoo;
import com.github.gabrielruiu.springsocial.yahoo.connect.YahooConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.quickstart.user.SecurityContext;
import org.springframework.social.quickstart.user.SimpleConnectionSignUp;
import org.springframework.social.quickstart.user.SimpleSignInAdapter;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Spring Social Configuration.
 * @author Craig Walls
 */
@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

	@Inject
	private DataSource dataSource;

	//
	// SocialConfigurer implementation methods
	//
	
	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
		cfConfig.addConnectionFactory(new YahooConnectionFactory(env.getProperty("yahoo.consumerKey"), env.getProperty("yahoo.consumerSecret")));
	}


	/**
	 * Singleton data access object providing access to connections across all users.
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		repository.setConnectionSignUp(new SimpleConnectionSignUp());
		return repository;
	}
	
	public UserIdSource getUserIdSource() {
		return new UserIdSource() {
			@Override
			public String getUserId() {
				return SecurityContext.getCurrentUser().getId();
			}
		};
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Yahoo yahoo(ConnectionRepository repository) {
		Connection<Yahoo> connection = repository.findPrimaryConnection(Yahoo.class);
		return connection != null ? connection.getApi() : null;
	}

	@Bean
	public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
		return new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, new SimpleSignInAdapter());
	}

}
