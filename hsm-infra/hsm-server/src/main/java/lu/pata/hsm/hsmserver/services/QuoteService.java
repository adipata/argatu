package lu.pata.hsm.hsmserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class QuoteService {
    private Logger log= LoggerFactory.getLogger(QuoteService.class);
    private List<String> quotes=new ArrayList<>();
    private Random rnd=new Random();

    @PostConstruct
    public void init(){
        File file = new File(
                getClass().getClassLoader().getResource("quotes.txt").getFile()
        );

        StringBuilder sb=new StringBuilder();

        try(BufferedReader br=new BufferedReader(new FileReader(file))){
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if(line.trim().length()>0){
                    sb.append(line+"\r\n");
                } else {
                    quotes.add(sb.toString());
                    sb=new StringBuilder();
                }
            }

            if(sb.length()>0) quotes.add(sb.toString()); //Add last quote
        } catch(IOException e){
            log.error(e.getMessage());
        }

        log.info("Loaded quotes: "+quotes.size());
    }

    public String getQuote(){
        return quotes.get(rnd.nextInt(quotes.size()));
    }
}
