package models.trading;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.Random;

public class EnproFirm extends Agent<TradingModel.Globals>{
    @Variable
    public double demand;
    public double buf;
    public double tprice;
    public double cost;
    public double tax;
    @Variable
    public double ecost;
    public double fcost;
    public double capFactor;
    public double wage;
    public double AET;
    public double enRate;
    @Variable
    public double enPrice;
    public int count;
    public double ctax;
    public double emFactor;
    @Variable
    public double carbon_emi;
    @Override
    public void init() {
        demand=10;
        buf=0.15;
        capFactor = 0.85;
        enRate=0.074;
        ctax=12;
        emFactor=0.5;
        enPrice=50;
        carbon_emi=1000;
    }
    public static Action<EnproFirm> sendEnergyPrice =

            Action.create(EnproFirm.class, enproFirm ->{
                double sumCost=0;
                double sumEnrate=0;

                enproFirm.count+=1;
                enproFirm.demand=(1+enproFirm.buf)*enproFirm.demand;
                enproFirm.getMessagesOfType(Messages.sendTechprice.class).forEach(mes -> {

                    enproFirm.tprice=mes.techPrice;
                    enproFirm.AET=mes.aet;

                });

                sumEnrate+=Math.pow(1+enproFirm.enRate,-enproFirm.count);
                enproFirm.ecost = enproFirm.tprice/(8760*enproFirm.capFactor)+(enproFirm.wage/enproFirm.AET*100000)*sumEnrate+(enproFirm.emFactor/1000)*sumEnrate*enproFirm.ctax;
                enproFirm.cost=enproFirm.demand*enproFirm.tprice;
                enproFirm.carbon_emi+=enproFirm.AET;
                sumCost+=enproFirm.ecost;
                double shuffle=enproFirm.getPrng().gaussian(0,1).sample();
                enproFirm.enPrice+=enproFirm.enPrice*0.011;
//                if (enproFirm.getGlobals().year_num<30){
//                    enproFirm.enPrice-=0.1*sumCost/(enproFirm.count-10*shuffle);
//                }
//                else{
//                    enproFirm.enPrice+=0.03*sumCost/(enproFirm.count-3*shuffle);
//                }


                enproFirm
                        .getLinks(Links.EpFtoCoF.class)
                        .send(Messages.SendenergyPrice.class, (msg, link) -> {
                            msg.price = enproFirm.enPrice;


                        });
                enproFirm
                        .getLinks(Links.EpFtoLabor.class)
                        .send(Messages.SendenergyPrice.class, (msg, link) -> {
                            msg.price = enproFirm.enPrice;


                        });
                enproFirm
                        .getLinks(Links.EpFtoCaF.class)
                        .send(Messages.SendenergyPrice.class, (msg, link) -> {
                            msg.price = enproFirm.enPrice;


                        });

                enproFirm
                        .getLinks(Links.EpFtoGovern.class)
                        .send(Messages.SendenergyPrice.class, (msg, link) -> {
                            msg.price = enproFirm.enPrice;


                        });
                enproFirm.getDoubleAccumulator("energyPrice").add(enproFirm.enPrice);

            });

    public static Action<EnproFirm> sendTax =

            Action.create(EnproFirm.class, enproFirm ->{
                enproFirm.tax = enproFirm.getGlobals().c_tax*enproFirm.cost*enproFirm.demand*0.15+enproFirm.ecost*0.018;
                enproFirm
                        .getLinks(Links.EpFtoGovern.class)
                        .send(Messages.sendTax.class, (msg, link) -> {
                            msg.EpF_tax = enproFirm.tax;


                        });
            });

    public static Action<EnproFirm> sendEmissions =

            Action.create(EnproFirm.class, enproFirm ->{
                Random r = new Random();

                if(enproFirm.getGlobals().mode==2){
                    enproFirm.carbon_emi += enproFirm.carbon_emi*0.018+(1 - 2 * r.nextDouble())*0.011*enproFirm.carbon_emi;

                }
                else if(enproFirm.getGlobals().mode==1){
                    enproFirm.carbon_emi += enproFirm.carbon_emi*0.018-(1 - 2 * r.nextDouble())*0.1*enproFirm.carbon_emi;

                }
                else{
                    if(enproFirm.getGlobals().year_num<30){
                        enproFirm.carbon_emi += enproFirm.carbon_emi*0.018+(1 - 2 * r.nextDouble())*0.011*enproFirm.carbon_emi;

                    }
                    else{
                        enproFirm.carbon_emi += enproFirm.carbon_emi*0.018-(1 - 2 * r.nextDouble())*0.1*enproFirm.carbon_emi;

                    }

                }
                enproFirm
                        .getLinks(Links.EpFtoGovern.class)
                        .send(Messages.sendEmissions.class, (msg, link) -> {
                            msg.emissions = enproFirm.carbon_emi;


                        });
            });
}



