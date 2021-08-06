package models.trading;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;
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
    public double carbon_em;
    @Override
    public void init() {
        demand=10;
        buf=0.15;
        capFactor = 0.85;
        enRate=0.074;
        ctax=12;
        emFactor=0.750;
        enPrice=0.1;
        carbon_em=500;
    }
    public static Action<EnproFirm> conductBehavior=

            Action.create(EnproFirm.class, enproFirm ->{
                double sumCost=0;
                double sumEnrate=0;

                enproFirm.count+=1;
                enproFirm.demand=(1+enproFirm.buf)*enproFirm.demand;
                enproFirm.carbon_em-=enproFirm.carbon_em*0.01;
                enproFirm.getMessagesOfType(Messages.sendTechprice.class).forEach(mes -> {

                    enproFirm.tprice=mes.techPrice;
                    enproFirm.AET=mes.aet;

                });

                sumEnrate+=Math.pow(1+enproFirm.enRate,-enproFirm.count);
                enproFirm.ecost = enproFirm.tprice/(8760*enproFirm.capFactor)+(enproFirm.wage/enproFirm.AET*100000)*sumEnrate+(enproFirm.emFactor/1000)*sumEnrate*enproFirm.ctax;
                enproFirm.cost=enproFirm.demand*enproFirm.tprice;
                enproFirm.tax = enproFirm.getGlobals().c_tax*enproFirm.cost*enproFirm.demand*0.15+enproFirm.ecost*0.018+enproFirm.carbon_em*20;
                sumCost+=enproFirm.ecost;
                enproFirm.enPrice=sumCost/enproFirm.count;

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
                        .send(Messages.sendTax.class, (msg, link) -> {
                            msg.EpF_tax = enproFirm.tax;


                        });

            });
}



