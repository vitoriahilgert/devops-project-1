package br.ufscar.dc.dsw.AA2.config;

import br.ufscar.dc.dsw.AA2.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.AA2.models.User;
import br.ufscar.dc.dsw.AA2.models.enums.UserRoleEnum;
import br.ufscar.dc.dsw.AA2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JwtService {
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private UserRepository userRepository;

    public String generateToken(User user) {
        Instant now = Instant.now();
        long expiry = 36000L;

        UserRoleEnum role = user.getRole();
        String scope;

        if (role == UserRoleEnum.ADMIN) {
            scope = "ROLE_ADMIN ROLE_TESTER";
        } else {
            scope = "ROLE_TESTER";
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("my-api")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getId().toString())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public UUID getIdFromToken(String token) {
        token = token.replace("Bearer ", "");
        Jwt jwt = jwtDecoder.decode(token);
        return UUID.fromString(jwt.getSubject());
    }

    public User getUserFromToken(String token) {
        UUID id = getIdFromToken(token);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
    }


}
