package com.alisimsek.specification;

import com.alisimsek.dto.request.UserSearchRequest;
import com.alisimsek.model.User;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {

    public static Specification<User> search(UserSearchRequest searchRequest) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<Predicate>();

            if (StringUtils.hasText(searchRequest.getFirstName())) {
                predicates.add(cb.like(cb.lower(root.get("firstName")),
                        "%" + searchRequest.getFirstName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(searchRequest.getLastName())) {
                predicates.add(cb.like(cb.lower(root.get("lastName")),
                        "%" + searchRequest.getLastName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(searchRequest.getUsername())) {
                predicates.add(cb.like(cb.lower(root.get("username")),
                        "%" + searchRequest.getUsername().toLowerCase() + "%"));
            }

            if (searchRequest.getUserType() != null) {
                predicates.add(cb.equal(root.get("userType"), searchRequest.getUserType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
