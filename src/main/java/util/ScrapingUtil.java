package util;

import dto.PartidaGoogleDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScrapingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
    private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
    private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-br";



    private static final String DIV_GOLS_EQUIPE_CASA = "div[class=imso_gs__tgs imso_gs__left-team]";
    private static final String DIV_GOLS_EQUIPE_VISITANTE = "div[class=imso_gs__tgs imso_gs__right-team]";
    private static final String ITEM_GOL = "div[class=imso_gs__gs-r]";
    private static final String DIV_PLACAR_EQUIPE_CASA = "div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]";
    private static final String DIV_PLACAR_EQUIPE_VISITANTE = "div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]";
    private static final String DIV_PENALIDADES = "div[class=imso_mh_s__psn-sc]";

    //informações equipe
    private static final String DIV_DADO_EQUIPE_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
    private static final String DIV_DADO_EQUIPE_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";

    //logos
    private static final String ITEM_LOGO = "img[class=imso_btl__mh-logo]";

    //dados partida
    private static final String DIV_PARTIDA_ANDAMENTO = "div[class=imso_mh__lv-m-stts-cont]";
    private static final String DIV_PARTIDA_ENCERRADA = "span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]";


    private static final String CASA = "casa";
    private static final String VISITANTE = "visitante";

    private static final String HTTPS = "https://";
    private static final String SRC = "src";
    private static final String SPAN = "span";
    private static final String PENALTIS = "Pênaltis";


    public PartidaGoogleDTO obtemInformacoesPartida(String url){
        PartidaGoogleDTO partida = new PartidaGoogleDTO();
        Document documento = null;
        try {
            documento = Jsoup.connect(url).get();
            String titulo = documento.title();

            System.out.println("Titulo " + titulo);
            LOGGER.info("Titulo da página: {}", titulo);

            StatusPartida statusPartida = obtemStatusPartida(documento);
            partida.setStatusParitda(statusPartida.toString());
            LOGGER.info("Status partida: {}", statusPartida);

            if (statusPartida != StatusPartida.PARTIDA_NAO_INICIADA){
                String tempoPartida = obetemTempoPartida(documento);
                partida.setTempoPartida(tempoPartida);
                LOGGER.info("Tempo partida: {}", tempoPartida);

                Integer placarEquipeCasa = recuperarPlacarEquipe(documento, DIV_PLACAR_EQUIPE_CASA);
                partida.setPlacarEquipeCasa(placarEquipeCasa);
                LOGGER.info("Placar da equipe da casa: {}", placarEquipeCasa);

                Integer placarEquipeVisitante = recuperarPlacarEquipe(documento, DIV_PLACAR_EQUIPE_VISITANTE);
                partida.setPlacarEquipeVisitante(placarEquipeVisitante);
                LOGGER.info("Placar da equipe da casa: {}", placarEquipeVisitante);

                String golsEquipeCasa = recuperarGolsEquipe(documento, DIV_GOLS_EQUIPE_CASA);
                partida.setGolsEquipeCasa(golsEquipeCasa);
                LOGGER.info("Gols equipe da casa: {}", golsEquipeCasa);
                String golsEquipeVisitante = recuperarGolsEquipe(documento, DIV_GOLS_EQUIPE_VISITANTE);
                partida.setGolsEquipeVisitante(golsEquipeVisitante);
                LOGGER.info("Gols equipe visitante: {}", golsEquipeVisitante);

                Integer placarEstendidoEquipeCasa = buscaPenalidades(documento, CASA);
                partida.setPlacarEstendidoEquipeCasa(placarEstendidoEquipeCasa);
                LOGGER.info("placar estendido equipe casa: {}", placarEstendidoEquipeCasa);
                Integer placarEstendidoEquipeVistante = buscaPenalidades(documento, VISITANTE);
                partida.setPlacarEstendidoEquipeVisitante(placarEstendidoEquipeVistante);
                LOGGER.info("placar estendido equipe visitante: {}", placarEstendidoEquipeVistante);

            }

            String nomeEquipeCasa = recuperarNomeEquipe(documento, DIV_DADO_EQUIPE_CASA);
            partida.setNomeEquipeCasa(nomeEquipeCasa);
            LOGGER.info("Nome da equipe da casa: {}", nomeEquipeCasa);

            String nomeEquipeVisitante = recuperarNomeEquipe(documento, DIV_DADO_EQUIPE_VISITANTE);
            partida.setNomeEquipeVisitante(nomeEquipeVisitante);
            LOGGER.info("Nome da equipe visitante : {}", nomeEquipeVisitante);

            String urlLogoEquipeCasa = recuperarLogoEquipe(documento, DIV_DADO_EQUIPE_CASA);
            partida.setUrlLogoEquipeCasa(urlLogoEquipeCasa);
            LOGGER.info("Logo da equipe da casa : {}", urlLogoEquipeCasa);

            String urlLogoEquipeVisitante = recuperarLogoEquipe(documento, DIV_DADO_EQUIPE_VISITANTE);
            partida.setUrlLogoEquipeVisitante(urlLogoEquipeVisitante);
            LOGGER.info("Logo da equipe da casa : {}", urlLogoEquipeVisitante);
            return partida;
        } catch (IOException e) {
            LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM JSOUP -> {}", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }



    public StatusPartida obtemStatusPartida(Document  document){
        //situacoes
        //1- partida nao iniciada
        //2- partida iniciada
        //3- partida encerrada
        //4- penalidades
        StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
        boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();
        if(!isTempoPartida){
            String tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
            statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
            if(tempoPartida.contains(PENALTIS)){
                statusPartida = StatusPartida.PARTIDA_PENALTIS;
            }
            LOGGER.info(tempoPartida);
        }
        isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA).isEmpty();
        if(!isTempoPartida){
            statusPartida = StatusPartida.PARTIDA_ENCERRADA;

        }
        LOGGER.info(statusPartida.toString());
        return statusPartida;
    }

    public String obetemTempoPartida(Document document){
        String tempoPartida = null;
        boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();

        if (!isTempoPartida){
            tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
        }

        isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA)
                .isEmpty();
        if (!isTempoPartida){
            tempoPartida = document.select(DIV_PARTIDA_ENCERRADA)
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

    private String recuperarNomeEquipe(Document documento, String itemHtml) {
        Element element = documento
                .selectFirst(itemHtml);
        String nomeEquipe = element.select(SPAN).text();

        return nomeEquipe;
    }



    private String recuperarLogoEquipe(Document documento, String itemHtml) {
        Element element = documento.selectFirst(itemHtml);
        String urlLogo = HTTPS + element.select(ITEM_LOGO).attr(SRC);
        return urlLogo;
    }

    private Integer recuperarPlacarEquipe(Document documento, String itemHtml) {
        String placarEquipe = documento.selectFirst(itemHtml)
                .text();
        return formataPlacarStringInteger(placarEquipe);
    }

    private String recuperarGolsEquipe(Document documento, String itemHtml) {
        List<String> golsEquipe = new ArrayList<>();
        Elements elements = documento.select(itemHtml)
                .select(ITEM_GOL);
        for (Element e : elements){
            String infoGol = e.select(ITEM_GOL).text();
            golsEquipe.add(infoGol);
        }
        return String.join(", ", golsEquipe);
    }

    private Integer buscaPenalidades(Document documento, String tipoEquipe) {
        boolean ispenalidades = documento.select(DIV_PENALIDADES).isEmpty();

        if (!ispenalidades){
            String penalidades = documento.select(DIV_PENALIDADES).text();
            String penalidadeCompleta = penalidades.substring(0, 5).replace(" ", "");
            String [] divisao = penalidadeCompleta.split("-");
            LOGGER.info("penalidades: {}", penalidades);
            return tipoEquipe.equals(CASA) ? formataPlacarStringInteger(divisao[0]) :
                    formataPlacarStringInteger(divisao[1]);
        }

        return null;
    }
    public Integer formataPlacarStringInteger(String placar){
        Integer valor;
        try {
           valor = Integer.parseInt(placar);
        }catch (Exception e){
            valor = 0;
        }
        return valor;
    }

    public String montaUrl(String nomeEquipeCasa, String nomeEquipeVisitante){
        try {
            String equipeCasa = nomeEquipeCasa.replace(" ", "+").replace("-", "+");
            String equipeVisitante = nomeEquipeVisitante.replace(" ", "+").replace("-", "+");
            return  BASE_URL_GOOGLE + equipeCasa + "+x+" + equipeVisitante + COMPLEMENTO_URL_GOOGLE;
        } catch (Exception e) {
            LOGGER.error("Erro: {}", e.getMessage());
        }
        return null;
    }
}
