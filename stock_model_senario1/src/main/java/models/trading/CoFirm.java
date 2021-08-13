package models.trading;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.Random;

public class CoFirm extends Agent<TradingModel.Globals> {
    @Variable
    public double useCost;     //
    @Variable
    public double produceCost;   //
    public double EIG;
    public double EIA;
    public double lp;       //labor productivity
    public double demand;
    public double stock;
    public double desDemand;    //desired inventories
    public double desProduction;    //desired level of production
    public double wage;
    public double enPrice;


    //TODO: pull these into the setup() phase of your model and expose them as inputs
    @Override
    public void init() {
        demand = 500;
        desDemand = 200;
        stock = 100;
        wage = 2000;
        EIG = 1;
        EIA = 1;
        lp = 10;

    }


    public static Action<CoFirm> receiveLPChange =
            Action.create(CoFirm.class, coFirm -> {
                coFirm.getMessagesOfType(Messages.LPChange.class).forEach(mes -> {
//                    Random r = new Random();
                    coFirm.lp = mes.averageLP1;
                    coFirm.lp = mes.averageLP1;
                });
            });

    public static Action<CoFirm> receiveWage =
            Action.create(CoFirm.class, coFirm -> {
                coFirm.getMessagesOfType(Messages.WageChange.class).forEach(mes -> {
                    coFirm.wage = mes.wage;
                });
            });

    public static Action<CoFirm> receiveEIChange =
            Action.create(CoFirm.class, coFirm -> {
                coFirm.getMessagesOfType(Messages.EIChange.class).forEach(mes -> {
                    coFirm.EIA = mes.EIA;
                    coFirm.EIG = mes.EIG;
                });
            });

    public static Action<CoFirm> receiveEnergyPrice =
            Action.create(CoFirm.class, coFirm -> {
                coFirm.getMessagesOfType(Messages.SendenergyPrice.class).forEach(mes -> {
                    coFirm.enPrice = mes.price;
                });
                coFirm.produceCost = (coFirm.wage / coFirm.lp) + coFirm.EIA * coFirm.getGlobals().enPrice * 1000;
                coFirm.useCost = coFirm.EIG * coFirm.enPrice;

                coFirm
                        .getLinks(Links.CoFtoGovern.class)
                        .send(Messages.sendTax.class, (msg, link) -> {
                            msg.CoF_tax = (coFirm.produceCost + coFirm.useCost) * coFirm.getGlobals().c_tax * 0.15;


                        });
            });
    public static Action<CoFirm> conductBehavior =
            Action.create(CoFirm.class, coFirm -> {
                coFirm.desProduction = coFirm.demand + coFirm.desDemand - coFirm.stock;
//                coFirm.getMessagesOfType(Messages.WageChange.class).forEach(mes -> {
//                    coFirm.wage = mes.wage;
//                });

//                coFirm.getMessagesOfType(Messages.LPChange.class).forEach(mes -> {
////                    Random r = new Random();
//
//                });

            });


}
