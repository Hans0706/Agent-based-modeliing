package models.trading;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.Random;

public class EntechFirm extends Agent<TradingModel.Globals>{
    @Variable
    public double AET; //labor productivity of energy production
    @Variable
    public double BET; //labor productivity of manufacturing the energy technology
    @Variable
    public double EFM; //the energy intensity of producing the energy technology
    @Variable
    public double techCost;
    @Variable
    public double techPrice;
    public double prate;
    public double w;
    public double demand;
    public double tax;

    public double lrD;
    public double lrS;
    public double LBD;
    public double LBS;



    @Override
    public void init() {
        AET=11.1;
        BET=22.3;
        EFM=38300;
        w=2000;
        prate=0.05;
        demand=10;
    }


    public static Action<EntechFirm> calcCost=

            Action.create(EntechFirm.class, entechFirm ->{
                Random r = new Random(12);
                double a;
                double fd = entechFirm.getPrng().beta(2,2).sample();
                entechFirm.lrD=(-Math.log(1-fd))/Math.log(2);
                entechFirm.LBD=Math.pow(1+0.01*r.nextDouble(),-fd);

                double fs = entechFirm.getPrng().beta(1,1).sample();
                entechFirm.lrS=(-Math.log(1-fs))/Math.log(2);
                entechFirm.LBS=Math.pow(1+0.01*r.nextDouble(),-fs);

                entechFirm.BET*=1/(entechFirm.LBD*entechFirm.LBS);
                entechFirm.AET*=1/(entechFirm.LBD*entechFirm.LBS);
                entechFirm.EFM*=(entechFirm.LBD*entechFirm.LBS);

                entechFirm.techCost=entechFirm.w/entechFirm.BET+entechFirm.EFM*entechFirm.getGlobals().enPrice;
                entechFirm.techPrice=(1+entechFirm.prate)*entechFirm.techCost;
                entechFirm
                        .getLinks(Links.EpFtoEtF.class)
                        .send(Messages.sendTechprice.class, (msg, link) -> {
                            msg.techPrice = entechFirm.techPrice;
                            msg.aet=entechFirm.AET;

                        });

            });

    public static Action<EntechFirm> sendTax=

            Action.create(EntechFirm.class, entechFirm ->{

                entechFirm.tax=entechFirm.getGlobals().c_tax*(entechFirm.techPrice-entechFirm.techCost)*entechFirm.demand;
                entechFirm
                        .getLinks(Links.EtFtoGovern.class)
                        .send(Messages.sendTax.class, (msg, link) -> {
                            msg.EtF_tax = entechFirm.tax;

                        });
            });

}

