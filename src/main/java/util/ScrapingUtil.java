package util;

import dto.PartidaGoogleDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ScrapingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
    private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
    private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-br";
    public static void main(String[] args) {
        String url = BASE_URL_GOOGLE + "palmeiras+x+corinthians+08/08/2020" + COMPLEMENTO_URL_GOOGLE;

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
            LOGGER.info("Titulo da página: ()", titulo);
        } catch (IOException e) {
            LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM JSOUP -> {}", e.getMessage());
            e.printStackTrace();
        }

        return partida;
    }
}
