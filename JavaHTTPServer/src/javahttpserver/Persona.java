package javahttpserver;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;

public class Persona 
{
    @XmlElement
    public String nome;
    @XmlElement
    public String cognome;
    @XmlElement
    public String anno;
    @XmlElement
    public String sesso;
    
    public Persona(String nome, String cognome, String anno, String sesso)
    {
        this.nome = nome;
        this.cognome = cognome;
        this.anno = anno;
        this.sesso = sesso;
    }
}
