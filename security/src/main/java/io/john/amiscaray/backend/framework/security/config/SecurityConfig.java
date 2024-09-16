package io.john.amiscaray.backend.framework.security.config;

import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;
import io.john.amiscaray.backend.framework.security.di.SecurityStrategy;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record SecurityConfig(SecurityStrategy strategy,
                             Map<String, List<Role>> securedEndpointRoles) {

    public SecurityConfig {

    }

    public static SecurityConfigBuilder builder() {
        return new SecurityConfigBuilder();
    }


    public static class SecurityConfigBuilder {
        private SecurityStrategy strategy;
        private ArrayList<String> securedEndpointRoles$key;
        private ArrayList<List<Role>> securedEndpointRoles$value;

        SecurityConfigBuilder() {
        }

        public SecurityConfigBuilder strategy(SecurityStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public SecurityConfigBuilder securePathUsingRoles(String path, List<Role> roles) {
            if (this.securedEndpointRoles$key == null) {
                this.securedEndpointRoles$key = new ArrayList<String>();
                this.securedEndpointRoles$value = new ArrayList<List<Role>>();
            }
            this.securedEndpointRoles$key.add(path);
            this.securedEndpointRoles$value.add(roles);
            return this;
        }

        public SecurityConfigBuilder securePath(String path) {
            if (this.securedEndpointRoles$key == null) {
                this.securedEndpointRoles$key = new ArrayList<String>();
                this.securedEndpointRoles$value = new ArrayList<List<Role>>();
            }
            this.securedEndpointRoles$key.add(path);
            this.securedEndpointRoles$value.add(List.of(Role.any()));
            return this;
        }

        public SecurityConfigBuilder securedEndpointRoles(Map<? extends String, ? extends List<Role>> securedEndpointRoles) {
            if (securedEndpointRoles == null) {
                throw new NullPointerException("securedEndpointRoles cannot be null");
            }
            if (this.securedEndpointRoles$key == null) {
                this.securedEndpointRoles$key = new ArrayList<String>();
                this.securedEndpointRoles$value = new ArrayList<List<Role>>();
            }
            for (final Map.Entry<? extends String, ? extends List<Role>> $lombokEntry : securedEndpointRoles.entrySet()) {
                this.securedEndpointRoles$key.add($lombokEntry.getKey());
                this.securedEndpointRoles$value.add($lombokEntry.getValue());
            }
            return this;
        }

        public SecurityConfigBuilder clearSecuredEndpointRoles() {
            if (this.securedEndpointRoles$key != null) {
                this.securedEndpointRoles$key.clear();
                this.securedEndpointRoles$value.clear();
            }
            return this;
        }

        public SecurityConfig build() {
            Map<String, List<Role>> securedEndpointRoles;
            switch (this.securedEndpointRoles$key == null ? 0 : this.securedEndpointRoles$key.size()) {
                case 0:
                    securedEndpointRoles = java.util.Collections.emptyMap();
                    break;
                case 1:
                    securedEndpointRoles = java.util.Collections.singletonMap(this.securedEndpointRoles$key.get(0), this.securedEndpointRoles$value.get(0));
                    break;
                default:
                    securedEndpointRoles = new java.util.LinkedHashMap<String, List<Role>>(this.securedEndpointRoles$key.size() < 1073741824 ? 1 + this.securedEndpointRoles$key.size() + (this.securedEndpointRoles$key.size() - 3) / 3 : Integer.MAX_VALUE);
                    for (int $i = 0; $i < this.securedEndpointRoles$key.size(); $i++)
                        securedEndpointRoles.put(this.securedEndpointRoles$key.get($i), (List<Role>) this.securedEndpointRoles$value.get($i));
                    securedEndpointRoles = java.util.Collections.unmodifiableMap(securedEndpointRoles);
            }

            return new SecurityConfig(this.strategy, securedEndpointRoles);
        }

        public String toString() {
            return "SecurityConfig.SecurityConfigBuilder(strategy=" + this.strategy + ", securedEndpointRoles$key=" + this.securedEndpointRoles$key + ", securedEndpointRoles$value=" + this.securedEndpointRoles$value + ")";
        }
    }
}
