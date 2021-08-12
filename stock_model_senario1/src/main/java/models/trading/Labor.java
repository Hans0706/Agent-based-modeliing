package models.trading;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;
import simudyne.core.functions.SerializableConsumer;

import java.util.Random;
import java.lang.Math;

public class Labor extends Agent<TradingModel.Globals> {

    //TODO: if you want to expose all of these variables in the console each one will need a @Variable annotation
    @Variable
    public double wages;     //wage rate
    public double number;   //household numbers
    public double income;   //annual income
    public double tax;      //tax rate
    public double cost;     //annual cost of thneed
    @Variable
    public double aveLp1;  //different between company3 todo
    @Variable
    public double aveLp2;
    public double EIG;
    public double tradingThresh;
    @Variable
    public double sellValue;
    public double buyValue;
    public double priceChange;
    @Variable
    public double stPrice;
    public double enPrice;
    public double carbon_em;

    @Override
    public void init() {
        tradingThresh = getPrng().gaussian(0, 1).sample();
        aveLp1 = getPrng().beta(1, 3).sample();
        aveLp2 = getPrng().beta(1, 3).sample();
        number = 50;
        wages = 2000;
        tax = 0.07;
        buyValue = 10;
        sellValue = 10;
        carbon_em = 15.2;
    }

    private static Action<Labor> action(SerializableConsumer<Labor> consumer) {
        return Action.create(Labor.class, consumer);
    }

    public static Action<Labor> processInformation() {
        return action(
                labor -> {
                    double informationSignal = labor.getGlobals().informationSignal;
                    labor.getMessagesOfType(Messages.EIChange.class).forEach(mes -> {

                        labor.EIG = mes.EIG;

                    });
                    labor.getMessagesOfType(Messages.SendenergyPrice.class).forEach(mes -> {
                        labor.enPrice = mes.price;
                    });
                    if (informationSignal > labor.tradingThresh || informationSignal < -labor.tradingThresh) {

                        if (informationSignal > 0) {
                            labor.buyValue += 1;
                            labor.buy();

                        } else {
                            labor.sellValue += 1;
                            labor.sell();

                        }
                    }
                    double demands = labor.buyValue - labor.sellValue;
                    labor.priceChange = (demands / labor.number) / labor.getGlobals().lambda;
                    labor.stPrice += labor.priceChange;
                    labor.income = (1 - labor.tax) * labor.wages;
                    labor.cost = labor.EIG * labor.enPrice;
                    labor
                            .getLinks(Links.LabortoGovern.class)
                            .send(Messages.sendTax.class, (msg, link) -> {
                                msg.Labor_tax = labor.tax * labor.wages;


                            });
                }

        );
    }

    public static Action<Labor> updateLP() {
        return action(
                labor -> {

                    double temp = labor.getGlobals().TempChange;
                    if (temp < 0) {
                        labor.aveLp1 = labor.aveLp1 * 1.1;
                        labor.aveLp2 = labor.aveLp1 * 1.25;
                    } else {
                        labor.aveLp1 = labor.aveLp1 * 0.95;
                        labor.aveLp2 = labor.aveLp1 * 0.83;
                    }
                    labor.getDoubleAccumulator("laborProductivity").add(labor.aveLp1);

                    labor
                            .getLinks(Links.CoFtoLabor.class)
                            .send(Messages.LPChange.class, (msg, link) -> {
                                msg.averageLP1 = labor.aveLp1;


                            });
                    labor
                            .getLinks(Links.CoFtoLabor.class)
                            .send(Messages.LPChange.class, (msg, link) -> {
                                msg.averageLP2 = labor.aveLp2;


                            });
                }

        );

    }

    public static Action<Labor> updateWage() {
        return action(
                labor -> {
                    labor.wages = labor.wages;
                    labor
                            .getLinks(Links.CoFtoLabor.class)
                            .send(Messages.WageChange.class, (msg, link) -> {
                                msg.wage = labor.wages;

                            });
                    labor
                            .getLinks(Links.LabortoCaF.class)
                            .send(Messages.WageChange.class, (msg, link) -> {
                                msg.wage = labor.wages;

                            });
                    labor
                            .getLinks(Links.LabortoGovern.class)
                            .send(Messages.WageChange.class, (msg, link) -> {
                                msg.wage = labor.wages;

                            });
                }

        );


    }

    private void buy() {
        getDoubleAccumulator("buys").add(buyValue);
//        getLinks(Links.TradeLink.class).send(Messages.BuyOrderPlaced.class);
    }

    private void sell() {
        getDoubleAccumulator("sells").add(sellValue);
//        getLinks(Links.TradeLink.class).send(Messages.SellOrderPlaced.class);
    }

}
