package util;

import dto.PartidaGoogleDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ScrapingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
    private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
    private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-br";
    public static void main(String[] args) {
        String url = BASE_URL_GOOGLE + "botafogo+x+aurora" + COMPLEMENTO_URL_GOOGLE;

        ScrapingUtil scraping = new ScrapingUtil();

        scraping.obtemInformacoesPartida(url);

    }

    public PartidaGoogleDTO obtemInformacoesPartida(String url){
        PartidaGoogleDTO partida = new PartidaGoogleDTO();
        Document documento = null;
        try {
            documento = Jsoup.connect(url).get();
            String titulo = documento.title();

            System.out.println("Titulo " + titulo);
            LOGGER.info("Titulo da página: {}", titulo);

            StatusPartida statusPartida = obtemStatusPartida(documento);
            LOGGER.info("Status partida: {}", statusPartida);

            if (statusPartida != StatusPartida.PARTIDA_NAO_INICIADA){
                String tempoPartida = obetemTempoPartida(documento);
                LOGGER.info("Tempo partida: {}", tempoPartida);

            }

            String nomeEquipeCasa = recuperarNomeEquipeCasa(documento);
            LOGGER.info("Nome da equipe da casa: {}", nomeEquipeCasa);

            String nomeEquipeVisitante = recuperarNomeEquipeVisitante(documento);
            LOGGER.info("Nome da equipe visitante : {}", nomeEquipeVisitante);

            String urlLogoEquipeCasa = recuperarLogoEquipeCasa(documento);
            LOGGER.info("Logo da equipe da casa : {}", urlLogoEquipeCasa);

            String urlLogoEquipeVisitante = recuperarLogoEquipeVisitante(documento);
            LOGGER.info("Logo da equipe da casa : {}", urlLogoEquipeVisitante);

        } catch (IOException e) {
            LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM JSOUP -> {}", e.getMessage());
            e.printStackTrace();
        }

        return partida;
    }




    public StatusPartida obtemStatusPartida(Document  document){
        //situacoes
        //1- partida nao iniciada
        //2- partida iniciada
        //3- partida encerrada
        //4- penalidades
        StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
        boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
        if(!isTempoPartida){
            String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
            statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
            if(tempoPartida.contains("Pênaltis")){
                statusPartida = StatusPartida.PARTIDA_PENALTIS;
            }
            LOGGER.info(tempoPartida);
        }
        isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
        if(!isTempoPartida){
            statusPartida = StatusPartida.PARTIDA_ENCERRADA;

        }
        LOGGER.info(statusPartida.toString());
        return statusPartida;
    }

    public String obetemTempoPartida(Document document){
        String tempoPartida = null;
        boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();

        if (!isTempoPartida){
            tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
        }

        isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]")
                .isEmpty();
        if (!isTempoPartida){
            tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]")
                    .first().text();
        }
        //LOGGER.info(corrigeTempoPartida(tempoPartida));
        return corrigeTempoPartida(tempoPartida);
    }

    public String corrigeTempoPartida(String tempo){
        if(tempo.contains("'")){
            return tempo.replace("'", " min");
        }else {
            return tempo;
        }

    }

    private String recuperarNomeEquipeCasa(Document documento) {
        Element element = documento
                .selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
        String nomeEquipe = element.select("span").text();

        return nomeEquipe;
    }

    private String recuperarNomeEquipeVisitante(Document documento) {
        Element element = documento
                .selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
        String nomeEquipe = element.select("span").text();
        return nomeEquipe;
    }

    private String recuperarLogoEquipeCasa(Document documento) {
        Element element = documento.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
        String urlLogo = "https://" + element.select("img[class=imso_btl__mh-logo]").attr("src");
        return urlLogo;
    }

    private String recuperarLogoEquipeVisitante(Document documento) {
        Element element = documento.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
        String urlLogo = "https://" + element.select("img[class=imso_btl__mh-logo]").attr("src");
        return urlLogo;
    }
}
