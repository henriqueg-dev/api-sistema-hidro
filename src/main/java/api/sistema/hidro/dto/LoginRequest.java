package api.sistema.hidro.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginRequest {
    private String email;
    private String senha;
}