package api.sistema.hidro.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginResponse {
    private String token;
    private String nome;
    private String perfil;
}