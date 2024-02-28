package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaGoogleDTO implements Serializable {

    private static final long serialVersionId = 1l;

    private String statusParitda;
    private String tempoPartida;

    //informações para equipe da casa
    private String nomeEquipeCasa;
    private String urlLogoEquipeCasa;
    private Integer placarEquipeCasa;
    private String golsEquipeCasa;
    private String placarExtendidoEquipeCasa;

    //informações para equipe visitante
    private String nomeEquipeVisitante;
    private String urlLogoEquipeVisitante;
    private Integer placarEquipeVisitante;
    private String golsEquipeVisitante;
    private String placarExtendidoEquipeVisitante;


}
