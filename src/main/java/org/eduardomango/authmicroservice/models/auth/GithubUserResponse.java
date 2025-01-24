package org.eduardomango.authmicroservice.models.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GithubUserResponse {
        private int id;
        private String login; // Username
        private String email;
        private String name; // name from user
}
