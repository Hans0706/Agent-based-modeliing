package models.trading;

import simudyne.core.graph.Message;

public class Messages {


    public static class LPChange extends Message {
        public double averageLP1;
        public double averageLP2;
    }

    public static class WageChange extends Message {
        public double wage;
    }

    public static class EIChange extends Message {
        public double EIG;
        public double EIA;
        public double EIB;
    }

    public static class sendTechprice extends Message {
        public double techPrice;
        public double aet;

    }

    public static class sendTax extends Message {
        public double CaF_tax;
        public double CoF_tax;
        public double EpF_tax;
        public double EtF_tax;
        public double Labor_tax;
    }


    public static  class SendenergyPrice extends Message{
        public double price;
    }

}
