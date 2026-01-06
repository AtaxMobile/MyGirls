package com.ataxmobile.mygirls;

import java.util.Calendar;

public class Girls {
    private	int	id;
    private	String name;
    private	int D1;
    private int D2;
    private long fd;

    public Girls(int id, String n, int d1, int d2, long fd) {
        this.id = id;
        this.name = n;
        this.D1 = d1;
        this.D2 = d2;
        this.fd = fd;
    }

    public Girls(String n, int d1, int d2, long fd) {
        this.name = n;
        this.D1 = d1;
        this.D2 = d2;
        this.fd = fd;
    }

    // collection of sets and gets methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getD1() {
        return D1;
    }

    public void setD1(int d1) {
        this.D1 = d1;
    }

    public int getD2() {
        return D2;
    }

    public void setD2(int d2) {
        this.D2 = d2;
    }

    public long getFD() {
        return fd;
    }

    public void setFD(long fd) {
        this.fd = fd;
    }

    public int getCycleDate(Calendar c){
        // diffs in secs
        // c is Today, c2 is first cycle day
        // TODO test it
        int d1 = this.D1; int d2 = this.D2; long c2 = this.fd;
        long sec1, sec2, diffS;
        int diffD;
        int f, n;

        Calendar tmpC1 = Calendar.getInstance(), tmpC2 = Calendar.getInstance();
        tmpC1.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0 );
        sec1 = (long) Math.floor((tmpC1.getTimeInMillis())/1000);
        tmpC2.setTimeInMillis(c2);
        tmpC2.set(tmpC2.get(Calendar.YEAR), tmpC2.get(Calendar.MONTH), tmpC2.get(Calendar.DAY_OF_MONTH), 0, 0, 0 );
        sec2 = (long) Math.floor((tmpC2.getTimeInMillis()/1000));
        // calculate day of cycle
        diffS = sec1-sec2;
        diffD = (int) Math.floor( diffS/60/60/24 );

        if( diffD >= 0 ) {
            f = (int) Math.floor(diffD/d1); n = diffD - f*d1; n = n+1;
        } else {
            diffD = diffD * (-1);
            f = (int) Math.ceil(((float)(diffD)/((float)(d1))));
            n = f*d1 - diffD; n = n+1;
        }
        return n;
    }

    public int getSub(Calendar c) {
//        Drawable img;
        int retVal = 0;
        int d1 = this.getD1(); int d2 = this.getD2(); long fd = this.getFD();
        int n, ovd;
        n = this.getCycleDate(c);
        ovd = (int) d1/2;

        if(n <= d2) retVal = 1;
        else if(n == ovd) retVal = 2;
        else if ( (n > (ovd-4)) && (n < (ovd+3)) ) retVal = 3;
        else retVal = 0;

        return retVal;
    }

    public int getPMS(Calendar c) {
        int retVal = 0;
        int d1 = this.getD1();
        int n = this.getCycleDate(c);

        if((n > (d1-5)) && (n <= d1)) retVal = 1;
        else retVal = 0;

        return retVal;
    }

}
