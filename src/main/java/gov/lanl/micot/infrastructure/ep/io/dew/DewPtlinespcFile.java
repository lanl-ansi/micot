package gov.lanl.micot.infrastructure.ep.io.dew;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.ep.model.dew.DewPtlinespcData;
import gov.lanl.micot.util.io.FileParser;

/**
 * A simple file structure for reading in ptlinespcdata
 * @author Russell Bent
 *
 */
public class DewPtlinespcFile extends FileParser {
  
  /**
   * gets all the data associated with ptlinespc
   * @param filename
   * @return
   */
  public Collection<DewPtlinespcData> getData(String filename) {
    ArrayList<DewPtlinespcData> data = new ArrayList<DewPtlinespcData>();
    
    readFile(filename);    
    for (int i = 1; i < fileLines.size(); ++i) {
      String line = fileLines.get(i);
      StringTokenizer tokenizer = new StringTokenizer(line, "\t");
      DewPtlinespcData d = new DewPtlinespcData();
      d.setStnam(tokenizer.nextToken().trim());
      d.setQmenudis(Integer.parseInt(tokenizer.nextToken()));
      d.setIlibrow(Integer.parseInt(tokenizer.nextToken()));
      d.setIptrow(Integer.parseInt(tokenizer.nextToken()));
      d.setIcmp(Integer.parseInt(tokenizer.nextToken()));
      d.setSoverhead(Integer.parseInt(tokenizer.nextToken()));
      d.setIovld(Integer.parseInt(tokenizer.nextToken()));
      d.setChconstgrad(tokenizer.nextToken());
      d.setChstructtyp(tokenizer.nextToken());
      d.setTmutspc(Integer.parseInt(tokenizer.nextToken()));
      d.setDxph1orr1(Double.parseDouble(tokenizer.nextToken()));       
      d.setDyph1orx1(Double.parseDouble(tokenizer.nextToken()));
      d.setDxph2orr0(Double.parseDouble(tokenizer.nextToken()));
      d.setDyph2orx0(Double.parseDouble(tokenizer.nextToken()));
      d.setDxph3ory0(Double.parseDouble(tokenizer.nextToken()));
      d.setDyph3ory1(Double.parseDouble(tokenizer.nextToken()));
      d.setDxneu(Double.parseDouble(tokenizer.nextToken()));
      d.setDyneu(Double.parseDouble(tokenizer.nextToken()));
      d.setDearth(Double.parseDouble(tokenizer.nextToken()));
      d.setSnumckts(Integer.parseInt(tokenizer.nextToken()));
      d.setSnumph(Integer.parseInt(tokenizer.nextToken()));
      d.setSnumneu(Integer.parseInt(tokenizer.nextToken()));
      d.setIvran(Integer.parseInt(tokenizer.nextToken()));
      d.setIbil(Integer.parseInt(tokenizer.nextToken()));
      d.setIbsl(Integer.parseInt(tokenizer.nextToken()));
      d.setDcfokv(Double.parseDouble(tokenizer.nextToken()));
      d.setDwthstdkv(Double.parseDouble(tokenizer.nextToken()));
      d.setStdesc(tokenizer.nextToken());
      d.setDcaptcstmult(Double.parseDouble(tokenizer.nextToken()));
      d.setSmnrmaintmon(Double.parseDouble(tokenizer.nextToken()));
      d.setSmjrmaintmon(Double.parseDouble(tokenizer.nextToken()));
      d.setSmnrmainthrs(Double.parseDouble(tokenizer.nextToken()));
      d.setSmjrmainthrs(Double.parseDouble(tokenizer.nextToken()));
      d.setDedate(tokenizer.nextToken());
      d.setLtotnumfail(Integer.parseInt(tokenizer.nextToken()));
      d.setLyrsexp(Integer.parseInt(tokenizer.nextToken()));
      d.setLtotinstal(Integer.parseInt(tokenizer.nextToken()));
      d.setLnumrepr(Integer.parseInt(tokenizer.nextToken()));
      d.setDtotreprtmhrs(Integer.parseInt(tokenizer.nextToken()));
      d.setDavgreprtmhrs(Integer.parseInt(tokenizer.nextToken()));
      d.setDavgfailpryr(Double.parseDouble(tokenizer.nextToken()));
      
      if (tokenizer.hasMoreElements()) {
        d.setIphcondindx(Integer.parseInt(tokenizer.nextToken()));
      }
      
      if (tokenizer.hasMoreElements()) {
        d.setIneucondindx(Integer.parseInt(tokenizer.nextToken()));
      }
      
      if (tokenizer.hasMoreElements()) {
        d.setIconfiglnkid(Integer.parseInt(tokenizer.nextToken()));
      }
      
      if (tokenizer.hasMoreElements()) { 
        d.setF45(tokenizer.nextToken());
      }
      
      if (tokenizer.hasMoreElements()) {
        d.setF46(tokenizer.nextToken());
      }
      
      if (tokenizer.hasMoreElements()) {
        d.setIndxSet(Integer.parseInt(tokenizer.nextToken()));                                    
      }
      
      data.add(d);      
    }
    
    return data;
  }
  
}
