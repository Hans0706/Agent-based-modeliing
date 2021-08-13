package models.trading;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public class Country extends Agent<TradingModel.Globals> {

    @Variable(name = "GDP")
    public double gdp_value = 40369;
    @Variable
    public double taxValue;
    public double w;
    public double old_t;
    public double old_w;
    public double ePrice;

    public void init() {
        taxValue = 0;
        gdp_value = 40369;
        w = 2000;
    }

    public static Action<Country> receiveWage =

            Action.create(Country.class, country -> {
                country.getMessagesOfType(Messages.WageChange.class).forEach(mes -> {
                    country.w = mes.wage;
                });
            });

    public static Action<Country> receiveTax =
            Action.create(Country.class, country -> {
                country.getMessagesOfType(Messages.sendTax.class).forEach(mes -> {
                    country.taxValue += mes.CaF_tax + mes.CoF_tax + mes.EpF_tax + mes.EtF_tax + mes.Labor_tax;
                });
            });

    public static Action<Country> receiveEnergyPrices =
            Action.create(Country.class, country -> {
                country.getMessagesOfType(Messages.SendenergyPrice.class).forEach(mes -> {
                    country.ePrice = mes.price;
                });
            });

    public static Action<Country> updateTax =
            Action.create(Country.class, country -> {
                country.old_t = country.taxValue;
                country.old_w = country.w;
                double t_change = 1;
                double w_change = 1;
                double g_change = 1;


                if (country.old_t != 0 && country.taxValue != 0) {
                    t_change = (country.taxValue - country.old_t) / country.old_t;
                    w_change = (country.w - country.old_w) / country.old_w;
                } else {
                    t_change = w_change = 0;
                }


                country.gdp_value += (0.017 + t_change + w_change) * country.gdp_value;
                country.getDoubleAccumulator("gdp").add(country.gdp_value);
                System.out.println(country.gdp_value);

            });


}
