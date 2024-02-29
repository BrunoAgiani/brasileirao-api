package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "equipe")
public class Equipe implements Serializable {

    private static final long serialVersionUID= 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipe_id")
    private Long id;
    @Column(name = "nome_equipe")
    private String nomeEquipe;
    @Column(name = "url_logo_equipe")
    private String urlLogoEquipe;

}
