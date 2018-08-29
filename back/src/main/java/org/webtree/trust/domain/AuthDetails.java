package org.webtree.trust.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDetails implements Serializable {
    private static final long serialVersionUID = 6425650941625914226L;
    private String username;
    private String password;
}