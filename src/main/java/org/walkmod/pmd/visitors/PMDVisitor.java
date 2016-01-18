package org.walkmod.pmd.visitors;

import java.io.File;
import java.io.FileInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.util.DomHelper;
import org.xml.sax.InputSource;

public class PMDVisitor<T> extends VoidVisitorAdapter<T> {

   private String configurationfile;

   private void parseCfg() throws Exception {
      File cfgFile = new File(configurationfile);
      FileInputStream is = new FileInputStream(cfgFile);
      try {
         InputSource in = new InputSource(is);
         in.setSystemId(configurationfile);
         Document doc = DomHelper.parse(in);
         NodeList rules = doc.getElementsByTagName("rule");
         
      } finally {
         is.close();
      }

   }

   public void setConfigurationFile(String configurationFile) throws Exception {
      this.configurationfile = configurationFile;
      parseCfg();
   }
}
