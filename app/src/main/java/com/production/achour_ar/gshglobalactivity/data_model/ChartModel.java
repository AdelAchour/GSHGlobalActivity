package com.production.achour_ar.gshglobalactivity.data_model;

import android.widget.LinearLayout;

public class ChartModel {

    String statutChart, titreChart;

    public ChartModel(String statutChart, String titreChart) {
        this.statutChart = statutChart;
        this.titreChart = titreChart;
    }

    public String getStatutChart() { return statutChart; }

    public String getTitreChart() {
        return titreChart;
    }
}
