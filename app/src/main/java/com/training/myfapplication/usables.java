package com.training.myfapplication;

public class usables {


    public String hebrewValue(float value,char money){
        String string = "";
        float newValue = value;
        if ( value <0)
            newValue =-value;
        if (value >= 1000000000){
            newValue = value / 1000000000;
            string =  " מיליארד";
        }
        else if(value >=1000000) {
            newValue = value / 1000000;
            string =  " מיליון";
        }
        else if(value >=1000) {
            newValue = value / 1000;
            string =  " אלף";
        }
        else
            string = " שקל";
        int decimalPoint = Float.toString(newValue).indexOf(".");
//        String format  = decimalPoint>=1? "%.0f":"%.1f";
        String format  = decimalPoint==1? "%.2f":decimalPoint==2?
                "%.1f":"%.0f";
        return String.format(format, newValue) + (money=='%'?"%":string);
    }
    public String percentValue(Float value,float yearSum) {
        float per = (value / yearSum)*100;
        if (per>10)
            return String.format("%.0f",per)+"%";
        return String.format("%.1f",per)+"%";
    }

}
