package javahttpserver;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class Anagrafica 
{
    @XmlElementWrapper
    @XmlElement(name="Persona")
    public ArrayList<Persona> elenco;
    
    public Anagrafica()
    {
        elenco = new ArrayList<>();
    }
    
    public void add(Persona p)
    {
        elenco.add(p);
    }
}
