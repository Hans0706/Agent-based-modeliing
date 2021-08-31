package models.trading;

import simudyne.core.graph.Link;

public class Links {

    public static class CaFtoCoF extends Link {

    }

    public static class CaFtoGovern extends Link {
        double val_change;
    }

    public static class CaFtoEpF extends Link{
        double vals;
    }
    public static class CaFtoLabor extends Link{
        double wage;
    }

    public static class CoFtoGovern extends Link {
        double vals;
    }

    public static class CoFtoEpF extends Link{
        double vals;
    }

    public static class CoFtoLabor extends Link{
        double vals;
    }

    public static class LabortoGovern extends Link {
        double vals;
    }

    public static class EpFtoGovern extends Link{
        double vals;
    }
    public static class EtFtoGovern extends Link{
        double vals;
    }

    public static class LabortoEpF extends Link{
        double vals;
    }

    public static class EpFtoEtF extends Link{
        double vals;
    }
    public static class LabortoCaF extends Link{
        double wage;
    }
    public static class EpFtoLabor extends  Link{
        double enprice;
    }
    public static class EpFtoCaF extends  Link{
        double enprice;
    }
    public static class EpFtoCoF extends  Link{
        double enprice;
    }
}
//        public List<String[]> temperature_list;

//        public List<String[]> readfile(String datafile) throws IOException {​
//            FileReader fileReader = new FileReader(datafile);
//            // create csvReader object and skip first Line
//            CSVReader csvReader = new CSVReaderBuilder(fileReader)
//                    .build();
//            return csvReader.readAll();
//        }​


//        public List<String[]> temperature_list;
//
//        public List<String[]> readfile(String datafile) throws IOException {
//            FileReader fileReader = new FileReader(datafile);
//            // create csvReader object and skip first Line
//            CSVReader csvReader = new CSVReaderBuilder(fileReader)
//                    .build();
//            return csvReader.readAll();
//        }

//    File csv = new File('temperature.csv');
//
//    CSVReader csvReader = new CSVReader (new InputStreamReader(csv.getInputStream()));
//    while ((record = csvReader.readNext()) != null) {
//      String [] header = reader.readNext();
//      for (String s : header) {
//        System.out.print(s + ",");
//      }
//      System.out.println("");
//
//      List<String[]> list = reader.readAll(); //read the second line
//
//    }
