package org.walkmod.pmd.visitors;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.util.DomHelper;
import org.xml.sax.InputSource;

public class PMDVisitor<T> extends VoidVisitorAdapter<T> {

   private String configurationfile;

   private Set<String> rules = new HashSet<String>();

   private void parseCfg(String config) throws Exception {
      File cfgFile = new File(config);
      FileInputStream is = new FileInputStream(cfgFile);
      try {
         InputSource in = new InputSource(is);
         in.setSystemId(configurationfile);
         Document doc = DomHelper.parse(in);
         NodeList rules = doc.getElementsByTagName("rule");
         int max = rules.getLength();
         for (int i = 0; i < max; i++) {
            Node rule = rules.item(i);
            if (rule instanceof Element) {
               Element elem = (Element) rule;
               if (elem.hasAttribute("ref")) {
                  String path = elem.getAttribute("ref");
                  URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
                  if (resource != null) {
                     File file = new File(resource.toURI());
                     parseCfg(file.getAbsolutePath());
                  }
                  NodeList children = elem.getChildNodes();
                  int limit = children.getLength();
                  for(int k = 0; k < limit; k++){
                     Node child = children.item(k);
                     if(child.getNodeName().equals("exclude")){
                        if(child instanceof Element){
                           Element exclude = (Element) child;
                           String excludeName = exclude.getAttribute("name");
                           this.rules.remove(excludeName);
                        }
                     }
                  }
                  
               } else {
                  if (elem.hasAttribute("name")) {
                     this.rules.add(elem.getAttribute("name"));
                  }
               }
            }
         }

      } finally {
         is.close();
      }

   }
   
   public Set<String> getRules(){
      return rules;
   }

   public void setConfigurationFile(String configurationFile) throws Exception {
      this.configurationfile = configurationFile;
      parseCfg(configurationfile);
   }
}
