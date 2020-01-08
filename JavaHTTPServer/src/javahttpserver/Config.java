package javahttpserver;

import java.io.File;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Config 
{
    @XmlElement
    public int port;
    @XmlElement
    public boolean verbose;
    @XmlElement
    public File web_root;
    @XmlElement
    public String default_file = "";
    @XmlElement
    public String file_not_found = "";
    @XmlElement
    public String method_not_supported = "";
    
}
